CREATE DATABASE IF NOT EXISTS railway CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE railway;

CREATE TABLE clients (
  client_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(80) NOT NULL,
  last_name  VARCHAR(80) NOT NULL,
  gov_id     VARCHAR(60) NOT NULL,
  age        INT NOT NULL,
  CONSTRAINT uq_clients_lastname_govid UNIQUE (last_name, gov_id)
);

CREATE TABLE routes (
  route_id        VARCHAR(50)  PRIMARY KEY,
  departure_city  VARCHAR(80) NOT NULL,
  arrival_city    VARCHAR(80) NOT NULL,
  departure_time  DATETIME     NOT NULL,
  arrival_time    DATETIME     NOT NULL,
  total_duration  VARCHAR(10)  NULL
);

CREATE TABLE trains (
  train_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  route_id          VARCHAR(50)  NOT NULL,
  train_type        VARCHAR(50)  NOT NULL,
  days_of_operation VARCHAR(80)  NOT NULL,
  CONSTRAINT fk_trains_route
    FOREIGN KEY (route_id) REFERENCES routes(route_id)
);

-- Ticket prices per route and class
CREATE TABLE IF NOT EXISTS ticket_prices (
  ticket_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  route_id    VARCHAR(50) NOT NULL,
  ticket_type VARCHAR(80) NOT NULL,
  ticket_rate DOUBLE NOT NULL,
  CONSTRAINT uq_ticket_route_class UNIQUE (route_id, ticket_type),
  CONSTRAINT fk_ticket_prices_route FOREIGN KEY (route_id) REFERENCES routes(route_id)
);

-- Trips (use same alphanumeric IDs as the app)
CREATE TABLE IF NOT EXISTS trips (
  trip_id        VARCHAR(32) PRIMARY KEY,
  route_id       VARCHAR(50) NOT NULL,
  departure_date DATE NOT NULL,
  CONSTRAINT fk_trips_route FOREIGN KEY (route_id) REFERENCES routes(route_id)
);

-- Reservations referencing trips, clients and optionally tickets
CREATE TABLE IF NOT EXISTS reservations (
  reservation_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  trip_id               VARCHAR(32) NOT NULL,
  route_id              VARCHAR(50) NOT NULL,
  client_id             BIGINT NULL,
  ticket_id             BIGINT NULL,
  passenger_first_name  VARCHAR(80) NOT NULL,
  passenger_last_name   VARCHAR(80) NOT NULL,
  passenger_gov_id      VARCHAR(60) NOT NULL,
  passenger_age         INT NOT NULL,
  ticket_class          VARCHAR(20) NOT NULL,
  ticket_number         BIGINT NOT NULL,
  CONSTRAINT fk_res_trip   FOREIGN KEY (trip_id)  REFERENCES trips(trip_id),
  CONSTRAINT fk_res_route  FOREIGN KEY (route_id) REFERENCES routes(route_id),
  CONSTRAINT fk_res_client FOREIGN KEY (client_id) REFERENCES clients(client_id),
  CONSTRAINT fk_res_ticket FOREIGN KEY (ticket_id) REFERENCES ticket_prices(ticket_id)
);

-- Purchased tickets (persisted purchases)
CREATE TABLE IF NOT EXISTS purchased_tickets (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  reservation_id BIGINT NOT NULL,
  trip_id       VARCHAR(32) NOT NULL,
  route_id      VARCHAR(50) NOT NULL,
  client_id     BIGINT NULL,
  ticket_id     BIGINT NULL,
  ticket_class  VARCHAR(20) NOT NULL,
  ticket_number BIGINT NOT NULL UNIQUE,
  ticket_price  DOUBLE NOT NULL,
  purchase_time DATETIME NOT NULL,
  CONSTRAINT fk_pt_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),
  CONSTRAINT fk_pt_route       FOREIGN KEY (route_id) REFERENCES routes(route_id),
  CONSTRAINT fk_pt_ticketprice FOREIGN KEY (ticket_id) REFERENCES ticket_prices(ticket_id)
);
