# Wint To Do Spring Boot Application

## H2 Database Console UI
The H2 Database Console UI is a web-based interface that allows you to interact with the H2 database used in the Wint To Do Spring Boot application. It provides a convenient way to execute SQL queries, view database tables, and manage the database schema.
### Accessing the H2 Database Console UI
To access the H2 Database Console UI, follow these steps:
1. Start the Wint To Do Spring Boot application.
2. Open your web browser and navigate to the following URL:
   ```
   http://localhost:8081/wint/h2-console/
   ```
   - We need to use /wint/h2-console/ because the application is configured with a context path of /wint in the application.properties file.
   - We are also using the port as 8081 because the application is configured to run on port 8081 in the application.properties file.
3. You will be presented with the H2 Database Console login page.
---
### Configuring the H2 Database Connection
On the H2 Database Console login page, you need to configure the database connection settings. Use the following details:
- **JDBC URL**: `jdbc:h2:mem:wint-todo-db` (Configured in application.properties)
- **Driver Class**: `org.h2.Driver`
- **User Name**: `sa`
- **Password**: (leave it blank)
### Using the H2 Database Console UI
Once you have entered the connection details, click on the "Connect" button to access the H2 Database Console UI. Here are some key features and functionalities:
- **SQL Query Execution**: You can execute SQL queries directly in the console. Simply type your SQL statements in the query editor and click on the "Run" button to execute them.
- **Table Management**: The console provides a list of database tables on the left-hand side. You can click on a table name to view its structure, data, and perform various operations such as inserting, updating, or deleting records.
- **Schema Management**: You can create, modify, or drop database schemas using SQL commands in the query editor.
- **Exporting Data**: The console allows you to export query results to various formats, such as CSV or SQL scripts.
### Important Notes
- The H2 Database Console UI is intended for development and testing purposes only. It is not recommended to use it in a production environment.
  - The H2 database used in this application is an in-memory database, which means that all data will be lost when the application is stopped or restarted.