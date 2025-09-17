#!/usr/bin/env bash

# The endpoint to which POST requests will be sent
ENDPOINT="http://localhost:8081/api/todos/create"
CLIENT_ID="spring-test-client"
# Generate a random base for request ids to ensure uniqueness
BASE_REQUEST_ID=$RANDOM

# Number of items to create
COUNT=12

for i in $(seq 1 $COUNT); do
    TITLE="Task #$i"
    DESCRIPTION="This is the description for task #$i."
    REQUEST_ID="${BASE_REQUEST_ID}-$i"

    # Create JSON body using jq for safety
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