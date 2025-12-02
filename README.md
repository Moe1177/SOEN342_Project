# SOEN342_Project

## Team members information

| Name               | Student Number | GitHub Username                                 |
| ------------------ | -------------- | ----------------------------------------------- |
| Mohamad Addasi     | 40278616       | [Moe1177](https://github.com/Moe1177)           |
| Mijan Ullah        | 40287584       | [mijanullah12](https://github.com/mijanullah12) |
| Emily Ng Youn Chen | 40285171       | [enyc24](https://github.com/enyc24)             |

## Start the project

Prerequisite: Install JDK ideally version 17-21 (required by Spring Boot 3.5).

1. Change into the application module directory

```bash
cd railway-network-system
```

2. Start the server (builds and runs the Spring Boot app)

- Windows (PowerShell):

```powershell
./mvnw.cmd spring-boot:run
```

- macOS/Linux:

```bash
./mvnw spring-boot:run
```

The application starts on http://localhost:8080 by default.

## Quickstart with Docker (MySQL + Adminer)

Prerequisites: Install Docker Desktop and Docker Compose.

1. Start the database and Adminer:

```bash
docker compose up -d
```

This launches:

- MySQL 8 on `localhost:3306` with DB `railway`, user `railway_user`, password `password`.
- Adminer UI on `http://localhost:8081` (System: MySQL, Server: `mysql` or `localhost`, User: `railway_user`, Password: `password`, Database: `railway`).

The file `mysql.sql` seeds base tables (including `ticket_prices` and `purchased_tickets`). The app will auto-create any missing tables via Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

2. Run the app against the Docker DB (use the `docker` Spring profile):

- Windows (PowerShell):

```powershell
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=docker
```

- macOS/Linux:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker
```

Alternatively, if running a packaged jar:

```bash
java -jar target/railway-network-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=docker
```

Notes:

- The app loads route/train/ticket price data from `src/main/resources/eu_rail_network.csv` on startup.
- Price catalog rows are stored in `ticket_prices`. Purchased tickets are stored in `purchased_tickets`.
- If you previously had price rows inside `tickets`, you can migrate them to `ticket_prices` and keep `purchased_tickets` for new purchases.

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
