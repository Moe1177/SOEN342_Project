# SOEN342_Project

## Team members information

| Name               | Student Number | GitHub Username                                 |
| ------------------ | -------------- | ----------------------------------------------- |
| Mohamad Addasi     | 40278616       | [Moe1177](https://github.com/Moe1177)           |
| Mijan Ullah        | 40287584       | [mijanullah12](https://github.com/mijanullah12) |
| Emily Ng Youn Chen | 40285171       | [enyc24](https://github.com/enyc24)             |


## Demo
📹 [View Demo](https://docs.google.com/videos/d/13vCyyGLjnE0goGhy45CtzC0Y2uU3SVxnOEub9cvqxrQ/edit?usp=sharing)

## Start the project

Prerequisites:

- Install JDK ideally version 17-21 (required by Spring Boot 3.5).
- Ensure the database is running (see [Database Setup](#database-setup) above).

1. Change into the application module directory

```bash
cd railway-network-system
```

2. Start the server (builds and runs the Spring Boot app)

- Windows (PowerShell):

```powershell
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=docker
```

- macOS/Linux:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker
```

The application starts on http://localhost:8080 by default.

**Notes:**

- The app loads route/train/ticket price data from `src/main/resources/eu_rail_network.csv` on startup.
- Price catalog rows are stored in `ticket_prices`. Purchased tickets are stored in `purchased_tickets`.
- The app will auto-create any missing tables via Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

## To run a specific iteration

Prerequisite: Install JDK ideally version 17-21 (required by Spring Boot 3.5).

1. Change into the desired iteration directory. Example:

```bash
cd railway-network-system/ITERATION_1/artifacts
```

2. Run the jar file (runs the Spring Boot app)

- Windows (PowerShell):

```powershell
java -jar railway-network-system-0.0.1-SNAPSHOT.jar
```

- macOS/Linux:

```bash
java -jar railway-network-system-0.0.1-SNAPSHOT.jar
```

The application starts on http://localhost:8080 by default.