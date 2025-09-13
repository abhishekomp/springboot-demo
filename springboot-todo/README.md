# 📝 Spring Boot Todo API with Swagger/OpenAPI

A simple, production-style REST API for managing Todo items, built with Spring Boot, JPA, and H2. Features robust API documentation and interactive testing via Swagger UI (SpringDoc OpenAPI).

---

## 🚀 Features

- **CRUD Endpoints** for Todo items
- **Mandatory Request Headers** for all endpoints (`X-Client-Id`, `X-Request-Id`)
- **Custom Response Headers** (e.g., `X-Processed-By`)
- **Centralized Error Handling** with detailed error responses
- **Interactive API Documentation** using Swagger UI
- **In-memory H2 Database** for easy local development and testing

---

## 🛠️ Tech Stack

- Java 17
- Spring Boot 3.5.x
- Spring Data JPA
- H2 Database
- SpringDoc OpenAPI (Swagger UI)
- Maven

---

## 📦 Getting Started

### 1. **Clone the Repository**

```bash
git clone https://github.com/your-username/springboot-todo.git
cd springboot-todo
```

### 2. **Build the Project**

```bash
mvn clean install
```

### 3. **Run the Application**

```bash
mvn spring-boot:run
```

The app will start on [http://localhost:8080](http://localhost:8080).

---

## 📚 API Documentation

### **Swagger UI**

- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### **OpenAPI Spec (JSON)**

- [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🧑‍💻 Example Endpoints

### **Headers Required for All Endpoints**

- `X-Client-Id`: Client identifier (e.g., `12345`)
- `X-Request-Id`: Request trace ID (e.g., `abcde`)

### **Sample: Get All Todos**

```http
GET /api/todos/all
Headers:
  X-Client-Id: 12345
  X-Request-Id: abcde
```

### **Sample: Get Todo by ID**

```http
GET /api/todos/{id}
Headers:
  X-Client-Id: 12345
  X-Request-Id: abcde
```

### **Sample: Create Todo**

```http
POST /api/todos/create
Headers:
  X-Client-Id: 12345
  X-Request-Id: abcde
Body (JSON):
{
  "title": "Buy groceries",
  "description": "Milk, eggs, bread"
}
```

---

## 🛡️ Error Handling

- **400 Bad Request**: Missing required headers or invalid input
- **404 Not Found**: Todo item not found
- **500 Internal Server Error**: Unexpected errors

All error responses follow a consistent schema (`ApiErrorResponse`).

---

## 📝 API Documentation Details

- **Request/Response Models** are annotated with `@Schema` for rich Swagger docs.
- **Headers** are documented using both `@RequestHeader` and `@Parameter`.
- **Response Headers** (e.g., `X-Processed-By`) are shown in Swagger UI.
- **Error Scenarios** are documented for each endpoint with `@ApiResponse`.

---

## 🗄️ Database

- Uses **H2 in-memory** database by default.
- Data is lost on restart.
- Access H2 Console at [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (if enabled).

---

## 🧩 Project Structure

```
src/main/java/com/example/todo/
├── controller/   # REST controllers
├── dto/          # Request/response models
├── exception/    # Custom exceptions & error responses
├── service/      # Business logic
└── SpringBootTodoApplication.java
```

---

## 📝 Customization

- **Swagger UI Path**: Change via `springdoc.swagger-ui.path` in `application.properties`.
- **API Metadata**: Customize title, description, version, etc., in `application.properties` or with `@OpenAPIDefinition`.

---

## 📖 References

- [SpringDoc OpenAPI](https://springdoc.org/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first.

---

## 📝 License

This project is licensed under the MIT License.

---

**Happy coding!**

# 🚀 Swagger/OpenAPI Integration in Spring Boot Project

## Overview

This project uses **SpringDoc OpenAPI** with **Swagger UI** to automatically generate, visualize, and interact with RESTful endpoints.  
The combo streamlines API documentation, testing, code generation, and client onboarding.

---

## What Are OpenAPI and Swagger?

- **OpenAPI Specification (OAS)**: Standard machine-readable format (YAML/JSON) for describing REST APIs. Tools, documentation, and codegen rely on it.
- **Swagger**: A suite of tools (UI, Editor, Codegen) built to consume and visualize OpenAPI specs.  
  Swagger UI is the most popular for trying out APIs in the browser.

**In practice:**  
SpringDoc OpenAPI generates OpenAPI docs (spec), and serves them with Swagger UI.

---

## Integration Steps

### 1. **Add SpringDoc Dependency in `pom.xml`**

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.11</version>
</dependency>
```

- **springdoc-openapi-starter-webmvc-ui:**  
  Auto-generates OpenAPI spec, and provides Swagger UI as `/swagger-ui.html`.

If Custom Path Is Required

If you want to move Swagger UI to a different path, you can set:
```
springdoc.swagger-ui.path=/my-custom-docs
```
in `application.properties`:

Then access at: http://localhost:8080/my-custom-docs
- Note: The OpenAPI spec remains at `/v3/api-docs`.
- Note: Avoid changing the default path unless necessary, as many tutorials and tools expect the standard locations.
- Note: The Spring boot app itself must be running to access these URLs.
---

### 2. **Accessing API Documentation and UI**

**Default URLs:**

| Resource           | URL                                      |
|--------------------|------------------------------------------|
| Swagger UI         | [`/swagger-ui.html`](http://localhost:8080/swagger-ui.html) or [`/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html) |
| Raw OpenAPI Spec   | [`/v3/api-docs`](http://localhost:8080/v3/api-docs)      |

- No need to append controller base paths.
- UI displays all detected endpoints with descriptions, schemas, request/response examples.

---

### 3. **Customizing API Metadata/Customizing General Documentation(Springdoc OpenAPI)**
You can configure API metadata (title, version, description, contact, etc.) and default settings in two main ways:

**Option 1: application.properties**
```properties
springdoc.api-docs.title=ToDo API
springdoc.api-docs.description=Manage your tasks and todos.
springdoc.api-docs.version=1.0.0
springdoc.api-docs.contact.name=Your Name
springdoc.api-docs.contact.email=your@email.com
springdoc.api-docs.license.name=Apache 2.0
springdoc.api-docs.license.url=https://www.apache.org/licenses/LICENSE-2.0.html
```

**Option 2: @OpenAPIDefinition Annotation**

Put this on your main application class or a configuration class:
```java
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
  info = @Info(
    title = "ToDo API",
    version = "1.0.0",
    description = "Manage your tasks and todos"
  )
)
@SpringBootApplication
public class TodoAppApplication {}
```
This provides a rich “About” section in the Swagger UI header.

---

### 4. **Annotating Controllers and Endpoints**

- **Purpose:** Enrich Swagger UI with clear descriptions, parameter docs, request/response schemas, status codes, and headers.


- **Key Annotations:**
  - `@Tag`: Group related endpoints (e.g., "Todos", "Users").
  - `@Operation`: Describe each endpoint (summary, description, tags).
  - `@ApiResponses` & `@ApiResponse`: Document possible responses (status codes, descriptions, schemas).
  - `@Parameter`: Document method parameters (query, path, header).
  - `@RequestBody`: Describe request body schema.
  - `@Header`: Document response headers.
  - `@Schema`: Annotate DTOs for detailed model documentation.
  - `@Hidden`: Hide internal or deprecated endpoints.
  - `@SecurityRequirement`: Indicate endpoints requiring authentication.
  - `@Content`: Define media types and schemas for request/response bodies.
  - `@ExampleObject`: Provide example values for request/response bodies.
  - `@ArraySchema`: Document arrays in request/response bodies.
  - `@Link`: Define links between operations for better navigation in the UI.
  - `@Callback`: Document asynchronous callbacks for operations that involve webhooks or event-driven interactions.
  - `@DiscriminatorMapping`: Use for polymorphic schemas to define how to differentiate between multiple types in a single schema.
  - `@ExternalDocumentation`: Link to external documentation for additional context or details about an endpoint or model.
  - `@Deprecated`: Mark endpoints or models as deprecated to inform users that they should avoid using them in favor of newer alternatives.
  
**Example:**

```java
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.parameters.*;
import io.swagger.v3.oas.annotations.headers.*;

@ApiResponses({
  @ApiResponse(
    responseCode = "201",
    description = "Created",
    content = @Content(schema = @Schema(implementation = TodoResponse.class)),
    headers = {
      @Header(
        name = "X-Response-Trace",
        description = "Echoes X-Request-ID for traceability.",
        schema = @Schema(type = "string")
      )
    }
  ),
  @ApiResponse(
    responseCode = "400",
    description = "Bad Request",
    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
  )
})
@PostMapping
public ResponseEntity<?> createTodo(
  @Parameter(description = "Request trace header", in = ParameterIn.HEADER, required = true)
  @RequestHeader("X-Request-ID") String requestId,
  
  @RequestBody TodoRequest request
) { ... }
```

**Tip:** Document all status codes and their schemas—Swagger UI will show example bodies for success *and* errors.

---

### 5. **Enriching DTOs (Request/Response Models)**

Annotate with `@Schema` for exhaustive docs:

```java
@Schema(description = "Request to create or update a ToDo item")
public class TodoRequest {
  @Schema(description = "Task title", example = "Buy groceries", required = true)
  private String title;

  @Schema(description = "Details", example = "Get milk, eggs, bread")
  private String description;
}
```
Do the same for response and error objects.

---

### 6. **Documenting Request Headers in UI**

To enable header fields in Swagger UI’s "Try it out":

- Annotate parameters using both:
    - `@RequestHeader(...)`
    - `@Parameter(in = ParameterIn.HEADER, ...)`

**Example:**
```java
@Parameter(description="Trace ID", in=ParameterIn.HEADER, required=true)
@RequestHeader("X-Request-ID") String requestId
```

---

### 7. **Error/Exception Scenario Gotchas**

**Problem:**  
Custom error responses (handled via `@RestControllerAdvice` and `@ExceptionHandler`) will not appear in Swagger UI unless **each endpoint** is annotated with `@ApiResponse` for error codes AND their response schema (e.g., `ApiErrorResponse.class`).

**Solution:**  
Always document error responses directly in the controller, even if returned by advice:

```java
@ApiResponses({
  @ApiResponse(
    responseCode = "404",
    description = "Not Found",
    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
  )
})
@GetMapping("/{id}")
public ResponseEntity<?> getTodoById(@PathVariable Long id) { ... }
```

---

## Common Issues & Gotchas

- **Swagger UI not at `/api/todos/swagger-ui.html`!**  
  Always access at root: `/swagger-ui.html` or `/swagger-ui/index.html`
- **Missing header fields in UI:**  
  Annotate both with `@RequestHeader` and `@Parameter`.
- **Error scenarios not displayed correctly:**  
  Must add `@ApiResponse` with your error schema for each endpoint.
- **Global headers:**  
  Not natively supported except for authentication. Document on each endpoint.
- **Swagger not updating:**  
  Restart your app after changing controller or DTO annotations.
- **Use DTOs with `@Schema`:**  
  Annotated models are more readable and informative in Swagger UI.

---

## Grouping Endpoints, Tags, and Security
- Use `@Tag` on controllers to group related endpoints.
- Use `@SecurityRequirement` to indicate endpoints requiring auth (e.g., JWT, OAuth2).
- Define security schemes in `@OpenAPIDefinition` or `application.properties`.
- Use @Hidden to hide internal endpoints.
- Refer to [SpringDoc Security](https://springdoc.org/#security) for details.
```java
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Todos", description = "Operations about todos")
@RestController
@RequestMapping("/api/todos")
public class TodoController {/** ... **/}
```

---

## References

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://www.openapis.org/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
- [Swagger/OpenAPI Annotations Guide](https://github.com/swagger-api/swagger-core/wiki/annotations)

---

## Need More Examples?

If you’re adding authentication, paginated responses, file uploads, or richer error types—extend the annotations correspondingly for full visibility in Swagger UI.

---

**Happy documentation!**