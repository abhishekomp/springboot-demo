#!/usr/bin/env bash
mvn spring-boot:run &
sleep 15  # Give app time to start, adjust as necessary
./createNTodos.bash num_todos=20