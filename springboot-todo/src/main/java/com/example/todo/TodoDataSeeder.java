package com.example.todo;

import com.example.todo.model.TodoEntity;
import com.example.todo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the database with demo todo items on application startup.
 * Only runs if the 'dev' profile is active (remove @Profile to always run).
 * The seeder checks if the repository is empty before seeding to avoid duplicates.
 * The profile can be activated via application.properties or command line:
 * --spring.profiles.active=dev
 * Via IntelliJ: Run -> Edit Configurations -> VM Options: -Dspring.profiles.active=dev
 * Via Maven: mvn spring-boot:run -Dspring-boot.run.profiles=dev
 * Via Gradle: ./gradlew bootRun --args='--spring.profiles.active=dev'
 * Via jar: java -jar -Dspring.profiles.active=dev yourapp.jar
 */
@Component
@Profile("dev") // Optional: only runs on 'dev' profile (omit if you want always)
public class TodoDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TodoDataSeeder.class);
    private final TodoRepository todoRepository;

    // read the count of todos to create from application properties
    @Value("${app.initial-todo-count:12}") // default to 12 if not set
    private int todoCount;

    private final int MAX_TODOS = 12; // max 12 demo todos available

    public TodoDataSeeder(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("Seeding demo todos...");
        // Check if repository is empty to avoid duplicate seeding
        if (todoRepository.count() > 0) {
            logger.info("Todos already exist, skipping seeding.");
            return;
        }
        List<TodoEntity> createdTodos = createDemoTodos(todoCount);
        // Save all created todos to repository
        todoRepository.saveAll(createdTodos);
        // Log the count of created todos
        logger.debug("Created {} demo todos.", createdTodos.size());
        // debug log each created todo
        createdTodos.forEach(todo -> logger.debug("Created todo: {}", todo));

        logger.info("Seeding complete. Created {} demo todos.", todoCount);
        // Create and save demo todos
//        List<TodoEntity> demoTodos = List.of(
//                createTodo("Buy groceries", "Milk, eggs, bread, and fruits"),
//                createTodo("Workout", "1 hour gym session"),
//                createTodo("Read book", "Finish chapter 5 of 'Effective Java'"),
//                createTodo("Call Mom", "Check in and see how she's doing"),
//                createTodo("Pay bills", "Electricity and internet bills due this week"),
//                createTodo("Plan weekend trip", "Research destinations and accommodations"),
//                createTodo("Clean the house", "Vacuum and dust all rooms"),
//                createTodo("Finish project report", "Complete the final draft for submission"),
//                createTodo("Attend yoga class", "Evening session at 6 PM"),
//                createTodo("Organize workspace", "Declutter desk and arrange files"),
//                createTodo("Go for a walk", "30 minutes in the park"),
//                createTodo("Bake cake", "Try new chocolate cake recipe")
//        );
        // Save all demo todos to repository
        //todoRepository.saveAll(demoTodos);
        // Log the count of seeded todos
        //logger.info("Seeded {} demo todos.", todoRepository.count());
    }

    // Create upto 12 demo todos with title and description. Configurable count
    // Method for creating N number of todos
    private List<TodoEntity> createDemoTodos(int count) {
        return List.of(
                createTodo("Buy groceries", "Milk, eggs, bread, and fruits"),
                createTodo("Workout", "1 hour gym session"),
                createTodo("Read book", "Finish chapter 5 of 'Effective Java'"),
                createTodo("Call Mom", "Check in and see how she's doing"),
                createTodo("Pay bills", "Electricity and internet bills due this week"),
                createTodo("Plan weekend trip", "Research destinations and accommodations"),
                createTodo("Clean the house", "Vacuum and dust all rooms"),
                createTodo("Finish project report", "Complete the final draft for submission"),
                createTodo("Attend yoga class", "Evening session at 6 PM"),
                createTodo("Organize workspace", "Declutter desk and arrange files"),
                createTodo("Go for a walk", "30 minutes in the park"),
                createTodo("Bake cake", "Try new chocolate cake recipe")
        ).subList(0, Math.min(count, MAX_TODOS)); // Limit to available demo todos
    }

    /** Helper method to create a TodoEntity
     * @param title Title of the todo
     * @param description Description of the todo
     * @return TodoEntity object
     */
    private TodoEntity createTodo(String buyGroceries, String s) {
        TodoEntity todo = new TodoEntity();
        todo.setTitle(buyGroceries);
        todo.setDescription(s);
        return todo;
    }
}