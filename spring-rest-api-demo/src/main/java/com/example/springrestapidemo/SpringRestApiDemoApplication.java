package com.example.springrestapidemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringRestApiDemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringRestApiDemoApplication.class);

    public static void main(String[] args) {

        // Example log messages
        logger.info("Spring Boot Application is starting...");
        logger.debug("Debugging is enabled.");
        SpringApplication.run(SpringRestApiDemoApplication.class, args);
    }

}
