package com.example.winttodo;

import com.example.winttodo.model.TodoEntity;
import com.example.winttodo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
public class TodoDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TodoDataSeeder.class);
    private final TodoRepository todoRepository;

    @Value("${app.initial-todo-count:5}")
    private int seedCount;

    public TodoDataSeeder(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public void run(String... args) {
        if (todoRepository.count() > 0) {
            logger.info("Todos already exist, skipping seeding.");
            return;
        }

        int count = Math.max(1, Math.min(seedCount, 12));
        logger.info("Seeding {} demo todos...", count);

        // Example pool of fake user IDs
        long[] userIds = {101L, 102L, 103L, 104L, 105L};
        Random rand = new Random();

        for (int i = 1; i <= count; i++) {
            TodoEntity todo = new TodoEntity();
            todo.setTitle("Task #" + i);
            todo.setDescription("This is the description for task #" + i + ".");

            // Assign a random due date: 1–30 days in the future
            int daysToAdd = rand.nextInt(30) + 1;
            LocalDate dueDate = LocalDate.now().plusDays(daysToAdd);
            todo.setDueDate(dueDate);

            // Assign a random user from the pool
            long assignedUserId = userIds[rand.nextInt(userIds.length)];
            todo.setAssignedUserId(assignedUserId);

            todoRepository.save(todo);

            logger.info("Inserted demo todo: {}, due {}, assigned to {}", todo.getTitle(), dueDate, assignedUserId);
        }

        logger.info("Inserted {} todo(s) with random due dates and assignee.", count);
    }
}