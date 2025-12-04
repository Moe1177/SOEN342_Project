# SOEN342_Project

## Team members information

| Name               | Student Number | GitHub Username                                 |
| ------------------ | -------------- | ----------------------------------------------- |
| Mohamad Addasi     | 40278616       | [Moe1177](https://github.com/Moe1177)           |
| Mijan Ullah        | 40287584       | [mijanullah12](https://github.com/mijanullah12) |
| Emily Ng Youn Chen | 40285171       | [enyc24](https://github.com/enyc24)             |

## Demo

📹 [View Demo](https://docs.google.com/videos/d/13vCyyGLjnE0goGhy45CtzC0Y2uU3SVxnOEub9cvqxrQ/edit?usp=sharing)

## Database Setup

The application requires MySQL 8.0 to be running. We use Docker to set up the database.

### Prerequisites

Install Docker Desktop and Docker Compose:

- **Windows/macOS**: Download from [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)
- **Linux**: Install Docker Engine and Docker Compose following the [official documentation](https://docs.docker.com/engine/install/)

### Starting the Database

1. **Start the database and Adminer:**

   From the project root directory:

   ```bash
   docker compose up -d
   ```

   This launches:

   - MySQL 8 on `localhost:3306` with DB `railway`, user `railway_user`, password `password`.
   - Adminer UI on `http://localhost:8081` (System: MySQL, Server: `mysql` or `localhost`, User: `railway_user`, Password: `password`, Database: `railway`).

   The file `mysql.sql` automatically seeds base tables when the container starts. The app will auto-create any missing tables via Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

   **Note:** If you need to manually run the SQL code to create the tables, all the SQL statements are included in the `mysql.sql` file located at the root of the railway-network-system folder: `mysql.sql`

2. **Verify the database is running:**

   ```bash
   docker compose ps
   ```

3. **View database logs (if needed):**

   ```bash
   docker compose logs mysql
   ```

### Stopping the Database

To stop the database:

```bash
docker compose down
```

To stop and remove all data volumes:

```bash
docker compose down -v
```

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
