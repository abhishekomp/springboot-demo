#!/usr/bin/env bash

ENDPOINT="http://localhost:8081/api/todos/create"
CLIENT_ID="spring-test-client"
# Generate a random base for request ids
BASE_REQUEST_ID=$RANDOM

# Define your todos as pairs of title and description
titles=("Buy groceries" "Workout" "Read book" "Pay bills")
descriptions=("Milk, eggs, bread, and fruits" \
              "1 hour gym session" \
              "Finish chapter 5 of 'Effective Java'" \
              "Electric/gas/water bills for September")

for i in "${!titles[@]}"; do
    TITLE="${titles[$i]}"
    DESCRIPTION="${descriptions[$i]}"
    REQUEST_ID="${BASE_REQUEST_ID}-$i"

    JSON_DATA=$(jq -n --arg title "$TITLE" --arg desc "$DESCRIPTION" \
      '{title: $title, description: $desc}')

    echo "Sending: $JSON_DATA"

    curl -s -X POST "$ENDPOINT" \
        -H "Content-Type: application/json" \
        -H "X-Client-Id: $CLIENT_ID" \
        -H "X-Request-Id: $REQUEST_ID" \
        -d "$JSON_DATA"

    echo -e "\n-----------------------------"
done