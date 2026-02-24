\# Demo Java Application



A feature-rich Java command-line application built with Maven, featuring task management, calculator, database integration, and more.



\## Features



\- Interactive CLI mode

\- Task management (in-memory and persistent database)

\- Calculator with basic operations

\- Date and time display

\- H2 database with connection pooling

\- Professional logging with SLF4J/Logback



\## Prerequisites



\- Java 21 or higher

\- Maven 3.6+



\## Build and Run



```bash

\# Build the project

mvn clean package



\# Run in interactive mode

java -jar target/demo-1.0-SNAPSHOT-jar-with-dependencies.jar



\# Run specific commands

java -jar target/demo-1.0-SNAPSHOT-jar-with-dependencies.jar hello John

java -jar target/demo-1.0-SNAPSHOT-jar-with-dependencies.jar calc 10 + 5

