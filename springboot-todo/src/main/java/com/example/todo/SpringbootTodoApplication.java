package com.example.todo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "ToDo List API",
                version = "1.0.0",
                description = "REST API for managing tasks",
                contact = @Contact(name = "Abhishek OmPrakash", email = "abhishek2283@hotmail.com"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")
        )
)
@SpringBootApplication
public class SpringbootTodoApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringbootTodoApplication.class);

    public static void main(String[] args) {
        logger.info("To-Do app starting...");
        logger.debug("[Debugging log level is enabled] To-Do app starting...");
        SpringApplication.run(SpringbootTodoApplication.class, args);
    }

}
