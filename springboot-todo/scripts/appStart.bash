#!/usr/bin/env bash

# Simple script to start Spring Boot app, wait for /health, run todo creation script, and tail logs.
echo "Starting Spring Boot application script. Script started at: $(date)"

# --------- #0: Prerequisites ---------
echo "Checking prerequisites..."
if ! command -v mvn &> /dev/null
then
    echo "❌ Maven (mvn) could not be found. Please install Maven and try again."
    exit 1
else
    echo "✅ Maven is installed."
fi

# --------- #1: Move to project root (parent of scripts/ no matter where script called from) ---------
cd -- "$(dirname "$0")/.." || { echo "Failed to cd to project root"; exit 1; }
echo "Changed directory to project root: $(pwd)"

# --------- #2: CONFIGURATION ---------
APP_PORT=8081
SPRING_CMD="mvn spring-boot:run"
LOG_FILE="spring-boot-app.log"
WAIT_SCRIPT_SECONDS=1
MAX_WAIT_SECONDS=60

TODO_SCRIPT="./scripts/createNTodos.bash"
TODO_SCRIPT_ARGS="num_todos=4"
APP_HEALTH_URL="http://localhost:$APP_PORT/api/todos/health"   # <--- Use your custom health endpoint

echo "🔹 Script started at: $(date)"

# --------- #3: Port Pre-check ---------
if nc -z localhost $APP_PORT; then
    echo "❌ Port $APP_PORT is already in use. Please free the port and try again."
    exit 1
fi

# --------- #4: Start Spring Boot ---------
echo "Starting Spring Boot application with command: $SPRING_CMD"
echo "The logs will be written to $LOG_FILE"
# Start Spring Boot app in background, redirect output to log file. The log file will be created or overwritten.
# Log file will contain both stdout and stderr.
# If you want to append instead of overwrite, use >> instead of >.
# The logfile is located in the project root.
# Use nohup to ensure it keeps running if terminal is closed
# Use & to run in background
# Capture PID to manage process
# Redirect both stdout and stderr to log file
$SPRING_CMD > "$LOG_FILE" 2>&1 &
APP_PID=$!
if [ -z "$APP_PID" ]; then
    echo "❌ Failed to start Spring Boot application. Check $LOG_FILE for details."
    exit 1
fi

# Ensure Spring Boot app is killed on script exit
# or on Ctrl+C (SIGINT) or termination (SIGTERM)
trap "echo '✅ Stopping Spring Boot app (PID: $APP_PID)'; kill $APP_PID 2>/dev/null; exit" INT TERM

echo -e "\n🎉 Started Spring Boot (PID: $APP_PID), logs in $LOG_FILE"

# --------- #5: Wait for /health Endpoint ---------
wait_for_health() {
  local url=$1
  local max_wait=$2
  local waited=0
  echo "Waiting for $url to be ready..."
  until curl -sf "$url" > /dev/null; do
    sleep "$WAIT_SCRIPT_SECONDS"
    waited=$((waited + WAIT_SCRIPT_SECONDS))
    if [ "$waited" -ge "$max_wait" ]; then
      echo "❌ Timed out waiting for $url after $max_wait seconds."
      kill $APP_PID 2>/dev/null
      exit 1
    fi
    if ! ps -p $APP_PID > /dev/null; then
      echo "❌ Spring Boot app process has exited! Check $LOG_FILE for errors."
      exit 1
    fi
  done
  echo "✅ $url is ready."
}

echo "Checking application health endpoint..."
wait_for_health "$APP_HEALTH_URL" "$MAX_WAIT_SECONDS"
sleep 1  # Give some time for requests to be processed

# --------- #6: Run Todo Script ---------
echo "Creating todos using $TODO_SCRIPT..."
bash "$TODO_SCRIPT" $TODO_SCRIPT_ARGS

echo "✅ Todo creation script completed."


# --------- #7: Show Spring Boot Logs ---------
echo "Tailing application logs. Hit Ctrl+C to quit (Spring app will be stopped)."
tail -f "$LOG_FILE"

# Script will exit (cleanup Spring app) on Ctrl+C because of the trap above.
echo "🔹 Script ended at: $(date)"