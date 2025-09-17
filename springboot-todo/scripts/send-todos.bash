#!/usr/bin/env bash

ENDPOINT="http://localhost:8081/api/todos/create"
CLIENT_ID="spring-test-client"
# Generate a random base for request ids
BASE_REQUEST_ID=$RANDOM

# Define your todos as pairs of title and description.
# This has 12 entries to test pagination.
titles=("Buy groceries" "Workout" "Read book" "Pay bills"
        "Walk the dog" "Call mom" "Finish project" "Clean room"
        "Go running" "Cook dinner" "Watch a movie" "Plan vacation")
descriptions=("Milk, eggs, bread, and fruits"
              "1 hour gym session"
              "Finish chapter 5 of 'Effective Java'"
              "Electric/gas/water bills for September"
              "30 min at the park"
              "Sunday family call"
              "Complete the presentation slides"
              "Vacuum and dust"
              "5k around the block"
              "Try new pasta recipe"
              "Comedy or drama"
              "Book hotels and flights")

for i in "${!titles[@]}"; do
    TITLE="${titles[$i]}"
    DESCRIPTION="${descriptions[$i]}"
    REQUEST_ID="${BASE_REQUEST_ID}-$i"

    JSON_DATA=$(jq -n --arg title "$TITLE" --arg desc "$DESCRIPTION" \
      '{title: $title, description: $desc}')

    echo "Sending: $JSON_DATA"

    curl -s -w "\nHTTP Status: %{http_code}\n" \
        -X POST "$ENDPOINT" \
        -H "Content-Type: application/json" \
        -H "X-Client-Id: $CLIENT_ID" \
        -H "X-Request-Id: $REQUEST_ID" \
        -d "$JSON_DATA"

    echo -e "\n-----------------------------"
done