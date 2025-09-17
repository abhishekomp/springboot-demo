#!/usr/bin/env bash

# Usage:
#   ./send-todos.sh
#   ./send-todos.sh num_todos=18

ENDPOINT="http://localhost:8081/api/todos/create"
CLIENT_ID="spring-test-client"
BASE_REQUEST_ID=$RANDOM

# Default
NUM_TODOS=12

# Optional: Read num_todos=NNN from command line
for arg in "$@"; do
  case $arg in
    num_todos=*)
      NUM_TODOS="${arg#*=}"
      shift
      ;;
  esac
done

echo "Creating $NUM_TODOS todos..."

for i in $(seq 1 $NUM_TODOS); do
    TITLE="Task #$i"
    DESCRIPTION="This is the description for task #$i."
    REQUEST_ID="${BASE_REQUEST_ID}-$i"

    JSON_DATA=$(jq -n --arg title "$TITLE" --arg desc "$DESCRIPTION" \
      '{title: $title, description: $desc}')

    echo "Sending request #$i: $JSON_DATA"

    curl -s -w "\nHTTP Status: %{http_code}\n" \
        -X POST "$ENDPOINT" \
        -H "Content-Type: application/json" \
        -H "X-Client-Id: $CLIENT_ID" \
        -H "X-Request-Id: $REQUEST_ID" \
        -d "$JSON_DATA"

    echo -e "\n-----------------------------"
done