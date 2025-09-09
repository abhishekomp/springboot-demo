# Spring REST API Demo

A sample Spring Boot REST API demonstrating:
- Header validation
- Request body validation
- Global exception handling
- JPA/H2 database integration
- OpenAPI/Swagger documentation
- Layered architecture (Controller, Service, Repository)
- Unit and MVC tests with MockMvc

## Features

- **Spring Boot 3.5.5**: Modern Java backend framework.
- **RESTful Endpoints**: CRUD operations for `ResourceEntity`.
- **Header Validation**: Endpoints require custom headers like `X-Auth-Token` and `X-Request-Id`.
- **Request Validation**: Uses `@NotBlank` for request fields.
- **Global Exception Handling**: Custom error responses for missing headers and validation errors.
- **OpenAPI/Swagger**: Auto-generated API docs with required headers.
- **H2 In-Memory Database**: For easy local development and testing.
- **JPA**: Data persistence with Spring Data JPA.
- **Layered Design**: Clean separation of controller, service, and repository.
- **Comprehensive Logging**: Configurable log levels for different packages.
- **Testing**: Includes both `@WebMvcTest` and `@SpringBootTest` with MockMvc and Mockito.

## Project Structure

- `controller/`: REST endpoints and header validation.
- `service/`: Business logic and logging.
- `repository/`: JPA repository for `ResourceEntity`.
- `model/`: Entity and request DTOs with validation.
- `exception/`: Global exception handler for structured error responses.
- `config/`: OpenAPI/Swagger configuration.
- `resources/`: Application properties for main and test environments.
- `test/`: Unit and integration tests.

## API Documentation

- Swagger UI available at `/swagger-ui.html` (when running the app).
- OpenAPI config documents required headers globally.

## Running the Application

### Prerequisites

- Java 17+
- Maven

### Build and Run

```sh
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the packaged jar
java -jar target/spring-rest-api-demo-0.0.1-SNAPSHOT.jar
```

### H2 Console

- Access at `/h2-console` (enabled by default).

## Running Tests

- Run all tests:
  ```sh
  mvn test
  ```
- Run a specific test class:
  ```sh
  mvn -Dtest=ResourceControllerMvcTest test
  ```
- Run a specific test method:
  ```sh
  mvn -Dtest=ResourceControllerMvcTest#testCreateResource_Negative_MissingName_StructuredErrorInResponse test
  ```

## Logging Configuration

- Logging levels are set in `src/main/resources/application.properties`.
- For tests, you can override log levels by adding `src/test/resources/application.properties` or `application-test.properties`.
- Example to reduce log verbosity in tests:
  ```
  logging.level.com.example.springrestapidemo.exception=INFO
  ```

## Exception Handling

- **MissingRequestHeaderException**: Returns structured JSON with code `MISSING_HEADER`.
- **MethodArgumentNotValidException**: Returns structured JSON with code `VALIDATION_ERROR` and validation message.

## Valuable Q&A from Project Development

### How to validate headers in Spring Boot?
- Use `@RequestHeader` annotation in controller method parameters.
- Example:
  ```java
  @PostMapping("/api/resource")
  public ResponseEntity<Resource> createResource(
      @RequestHeader("X-Auth-Token") String authToken,
      @RequestHeader("X-Request-Id") String requestId,
      @Valid @RequestBody ResourceRequest request) {
      // method implementation
  }
  ```
--- See my example in the GET and POST endpoints in the controller.

### OpenAPI/Swagger: How to document required headers globally for all endpoints?
- Create a configuration class with `@Configuration` annotation.
- Define a `@Bean` method returning `OpenAPI` object.
- Use `Components` to add `Header` objects for each required header.
- Use `addParametersItem` to add headers to the global parameters list.
- Example:
  ```java
  @Configuration
  public class OpenApiConfig {
      @Bean
      public OpenAPI customOpenAPI() {
          return new OpenAPI()
              .components(new Components()
                  .addHeaders("X-Auth-Token", new Header().description("Authentication token").required(true).schema(new StringSchema()))
                  .addHeaders("X-Request-Id", new Header().description("Request identifier").required(true).schema(new StringSchema()))
              )
              .addParametersItem(new Header().$ref("#/components/headers/X-Auth-Token"))
              .addParametersItem(new Header().$ref("#/components/headers/X-Request-Id"));
      }
  }
  ```
- See my `OpenApiConfig` class for the complete example.
- This will ensure that all endpoints in the Swagger UI show these headers as required.
- You can customize the header names, descriptions, and other properties as needed.
- You can also add @Operation annotations on individual endpoints to provide additional details if necessary.
- This approach keeps your API documentation consistent and reduces redundancy.
- See my `OpenApiConfig` class in the `config` package for the complete implementation.
- You can also use @Parameter annotation on individual endpoints to provide additional details if necessary.
- See my controller class for examples of using @Parameter.
- The endpoints for Swagger UI are `/swagger-ui.html` or `/swagger-ui/index.html` depending on the Springdoc version.
- The OpenAPI JSON is available at `/v3/api-docs`.
- Swagger UI: http://localhost:8080/swagger-ui.html or http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- These endpoints are available when the application is running and are automatically configured by Springdoc.
- This endpoint is automatically exposed by SpringDoc when you add the springdoc-openapi-starter-webmvc-ui dependency.
- It returns the OpenAPI specification document in JSON (or YAML if you request it with Accept: application/yaml).
- No additional configuration is needed to enable it.
- The OpenAPI spec from /v3/api-docs is the source of truth for your REST API.
- Swagger UI uses this spec to generate the interactive API documentation.
- Tools like OpenAPI Generator or Swagger Codegen can take this JSON and generate client SDKs (Java, Python, JavaScript, etc.) or server stubs.
- This allows you to easily create clients or server implementations based on your documented API.
- Example client generation command: (This produces a Java client library for your API)
  ```sh
  openapi-generator-cli generate -i http://localhost:8080/v3/api-docs -g java -o /path/to/output
  ```
  ```sh
  openapi-generator-cli generate -i http://localhost:8080/v3/api-docs -g java -o ./client-sdk
  ```
- You can publish /v3/api-docs or a prettified Swagger UI so other teams know exactly how to use your API.
- As a developer, just open http://localhost:8080/swagger-ui.html (which internally uses /v3/api-docs).
- You can export /v3/api-docs to a file and use it for validation. Example in Maven build: curl http://localhost:8080/v3/api-docs -o openapi.json
  ```xml
  <plugin>
      <groupId>org.openapitools</groupId>
      <artifactId>openapi-generator-maven-plugin</artifactId>
      <version>6.2.1</version>
      <executions>
          <execution>
              <goals>
                  <goal>generate</goal>
              </goals>
              <configuration>
                  <inputSpec>http://localhost:8080/v3/api-docs</inputSpec>
                  <generatorName>java</generatorName>
                  <output>${project.build.directory}/generated-sources/openapi</output>
              </configuration>
          </execution>
      </executions>
  </plugin>
  ```
### Process of exporting /v3/api-docs to a file and using it for validation?
- You can use a simple curl command to export the OpenAPI specification to a file.
- Example command:
  ```sh
  curl http://localhost:8080/v3/api-docs -o openapi.json
  ```
- This command fetches the OpenAPI JSON from the running application and saves it to `openapi.json`.
- You can then use this file for various purposes:
  - **Documentation**: Share the file with other teams or use it to generate static documentation.
  - **Client Generation**: Use tools like OpenAPI Generator or Swagger Codegen to generate client SDKs or server stubs.
  - **Validation**: Use the file to validate your API against the specification during development or CI/CD.
- To automate this in your build process, you can add a Maven plugin configuration as shown in the previous answer.
- This ensures that the OpenAPI spec is always up-to-date and can be used for generating clients or validating the API.
- Make sure your application is running when you execute the curl command or the Maven build that fetches the spec.
- You can also integrate this into your CI/CD pipeline to automatically fetch and validate the OpenAPI spec on each build.
- This helps maintain consistency between your API implementation and its documentation.

### How to generate client SDKs or server stubs from /v3/api-docs?
- Use tools like OpenAPI Generator or Swagger Codegen. 
- brew install openapi-generator   # macOS
- Example command to generate a Java client SDK:
  ```sh
  openapi-generator-cli generate -i http://localhost:8080/v3/api-docs -g java -o /path/to/output
  ```
  -i openapi.json → input spec file
  -g java → target language
  -o ./client-sdk → output folder
- You can replace `http://localhost:8080/v3/api-docs` with a local file path if you have exported the spec to a file.
- Replace `-g java` with the desired language (e.g., python, javascript).
- The generated code will be placed in the specified output directory.
- You can customize the generation with additional flags and configuration files as needed.

### How to run tests and the app from Maven?

- Run all tests: `mvn test`
- Run a specific test: `mvn -Dtest=ClassName#methodName test`
- For example, to run testCreateResource_Negative_MissingName_StructuredErrorInResponse test method in the class ResourceControllerMvcTest `mvn -Dtest=ResourceControllerMvcTest#testCreateResource_Negative_MissingName_StructuredErrorInResponse test`
- Run the app: `mvn spring-boot:run` or `java -jar target/your-jar.jar`

### Why do I see long exception stack traces in test output?

- Because logging is set to `DEBUG` for exceptions, full stack traces are printed.
- To reduce verbosity, set the log level to `INFO` in your test properties.

### How to use different logging for tests?

- Place `application.properties` or `application-test.properties` in `src/test/resources` with higher log levels.
- Spring Boot will use these for tests, overriding main config.
- When you run tests with Maven (e.g., `mvn test`), Spring Boot automatically gives higher priority to property files in `src/test/resources` over those in `src/main/resources`.  
  If you add `application-test.properties` to `src/test/resources`, and your tests use the `test` profile (either by default or via `@ActiveProfiles("test")`), Spring Boot will load it and override values from `application.properties`.

**Default behavior:**
- If you only have `application.properties` in both locations, the one in `src/test/resources` will override the one in `src/main/resources` for tests.
- If you use `application-test.properties`, you must activate the `test` profile (e.g., with `@ActiveProfiles("test")` or `-Dspring.profiles.active=test`).

**Summary:**  
You do not need to do anything extra for `application.properties` in `src/test/resources`—it is picked up automatically for tests. For profile-specific files, activate the profile.

### How does Spring Boot pick up test properties?

- `src/test/resources/application.properties` automatically overrides `src/main/resources/application.properties` during tests.
- For profile-specific files (like `application-test.properties`), activate the profile with `@ActiveProfiles("test")` or `-Dspring.profiles.active=test`.

### This project presents the error response in a structured JSON format. How to achieve that?
- Use `@ControllerAdvice` to create a global exception handler.
- Define methods with `@ExceptionHandler` for specific exceptions.
- Return a custom error response object with fields like `code` and `message`.

### This project demonstrates 2 different type of returning the exception response. One is structured JSON and another is plain text. How to achieve that?
- For structured JSON, use `@ControllerAdvice` with `@ExceptionHandler` methods that return a custom error response object.
- For plain text, you can let Spring handle it by not defining a custom handler, or you can return a `ResponseEntity<String>` with the error message.
- See my controller and exception handler classes for examples.
- In the controller, you can return plain text directly in the method or let Spring handle it by throwing exceptions without a custom handler.
- In the exception handler, you can create a method that returns a `ResponseEntity<String>` for plain text responses.
- See the 2 POST endpoints in the controller for examples of both approaches.

### Usage of @Import in test classes?
- Use `@Import` to include specific configuration or beans needed for the test context.
- Example:
  ```java
  @WebMvcTest(controllers = ResourceController.class)
  @Import({ResourceService.class, GlobalExceptionHandler.class})
  public class ResourceControllerMvcTest {
      // test methods
  }
  ```
- This ensures that the `ResourceService` and `GlobalExceptionHandler` are available in the test context.
- This is useful when you want to test the controller in isolation but still need certain beans to be present.
- Spring Boot’s @WebMvcTest automatically detects and registers any @ControllerAdvice (like your GlobalRestExceptionHandler) in the test context, even if you do not explicitly import it with @Import. This is the default behavior to ensure global exception handling is available during controller tests.
- So, even with @Import(GlobalRestExceptionHandler.class) commented out, your GlobalRestExceptionHandler is still picked up and used in your test.

### Can we have multiple POST endpoints in one controller?
- Yes, you can define multiple `@PostMapping` methods in a single controller class.
- Yes, you can add another POST endpoint (e.g., /api/resources/alt) to demonstrate structured error messages for invalid request bodies. Multiple POST methods can coexist in your controller as long as their paths differ. Both can use the same ResourceRepository and ResourceService.
- Each method can handle different paths or request bodies as needed.
- Just ensure that the paths do not conflict and that each method has a unique signature.
- This is useful for grouping related operations in one controller.
- Example:
  ```java
  @RestController
  @RequestMapping("/api/resources")
  public class ResourceController {
      
      @PostMapping("/typeA")
      public ResponseEntity<Resource> createTypeA(@RequestBody TypeARequest request) {
          // handle Type A resource creation
      }

      @PostMapping("/typeB")
      public ResponseEntity<Resource> createTypeB(@RequestBody TypeBRequest request) {
          // handle Type B resource creation
      }
  }
  ```
### How to validate headers in Spring Boot?
- Use `@RequestHeader` annotation in controller method parameters.
- Example:
  ```java
  @PostMapping("/api/resource")
  public ResponseEntity<Resource> createResource(
      @RequestHeader("X-Auth-Token") String authToken,
      @RequestHeader("X-Request-Id") String requestId,
      @Valid @RequestBody ResourceRequest request) {
      // method implementation
  }
  ```
### How to handle missing headers globally?
- Use `@ControllerAdvice` with `@ExceptionHandler(MissingRequestHeaderException.class)`.
- Example:
  ```java
  @ControllerAdvice
  public class GlobalExceptionHandler {
        @ExceptionHandler(MissingRequestHeaderException.class)
        public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException ex) {
            ErrorResponse error = new ErrorResponse("MISSING_HEADER", ex.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
    ```
### How to validate request bodies in Spring Boot?
- Use `@Valid` annotation on the request body parameter.
- Use Jakarta Bean Validation annotations (e.g., `@NotBlank`, `@Size`) on DTO fields.
- Example:
  ```java
  public class ResourceRequest {
      @NotBlank(message = "Name is mandatory")
      private String name;
      // getters and setters
  }
  ```
### How to handle validation errors globally?
- Use `@ControllerAdvice` with `@ExceptionHandler(MethodArgumentNotValidException.class)
- Example:
  ```java
  @ControllerAdvice
  public class GlobalExceptionHandler {
      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
          String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
          ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", errorMessage);
          return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
      }
  }
  ```

## Technologies Used

- Java 17
- Spring Boot 3.5.5
- Spring Web, Spring Data JPA, H2 Database
- Jakarta Validation
- OpenAPI/Swagger (springdoc-openapi)
- JUnit 5, Mockito, MockMvc

## Author

- [Abhishek]

---

_This README includes not only the technical setup but also documents key learnings and Q&A from the development process for future reference._
```
