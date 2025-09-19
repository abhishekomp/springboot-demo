// src/main/java/com/example/winttodo/utils/RandomJsonGenerator.java
package com.example.winttodo.utils;

import com.example.winttodo.dto.TodoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class RandomJsonGenerator {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // Write dates as ISO-8601, not timestamps. For example, "2025-10-15" instead of [2025,10,15]
        mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT); // Enable pretty print
        // Generate and print a random TodoRequest as JSON
        System.out.println(mapper.writeValueAsString(randomTodoRequestInEnglish()));

        // Example output:
        // {"tags":["alpha","beta"],"title":"Random Title","description":"Random description text.","dueDate":"2025-10-15"}
        // {"title":"Et mollitia accusantium et autem.","description":"Quia fuga illo minus animi dolores fuga sapiente recusandae.","dueDate":[2025,10,16],"tags":["ut","est"]}
    }

    // Generate a random TodoRequest object with English text
    private static TodoRequest randomTodoRequestInEnglish() {
        Faker faker = new Faker(Locale.ENGLISH);
        TodoRequest req = new TodoRequest();
        //req.setTitle(faker.book().title()); // Realistic English title
        //req.setTitle(faker.name().fullName());
        req.setTitle(faker.company().catchPhrase()); // Realistic English title
        req.setDescription(faker.lorem().sentence(2)); // English phrase
        req.setDueDate(LocalDate.now().plusDays(faker.number().numberBetween(1, 30)));
        req.setTags(List.of(faker.music().genre(), faker.color().name())); // Use supported methods for tags
        return req;
    }

    // Generate a random TodoRequest object
    private static TodoRequest randomTodoRequest() {
        //Faker faker = new Faker();
        // Create a Faker for English locale so that the generated text is in English
        Faker faker = new Faker(Locale.ENGLISH);
        TodoRequest req = new TodoRequest();
        req.setTitle(faker.lorem().sentence(3)); // Faker.lorem() generates "lorem ipsum" placeholder text, not real English sentences.
        // To get actual English content, use other Faker methods like faker.book().title(), faker.lorem().sentence() (for short phrases), or faker.company().catchPhrase().
        req.setDescription(faker.lorem().sentence(8));
        req.setDueDate(LocalDate.now().plusDays(faker.number().numberBetween(1, 30)));
        req.setTags(List.of(faker.lorem().word(), faker.lorem().word()));
        return req;
    }
}