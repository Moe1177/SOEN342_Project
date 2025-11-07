CREATE DATABASE railway
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE railway;

CREATE TABLE clients (
  client_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(80) NOT NULL,
  last_name  VARCHAR(80) NOT NULL,
  gov_id     VARCHAR(60) NOT NULL,
  age		 VARCHAR(100) NOT NULL,
  CONSTRAINT uq_clients_lastname_govid UNIQUE (last_name, gov_id)
);

USE railway;

CREATE TABLE routes (
  route_id        VARCHAR(50)  PRIMARY KEY,
  departure_city  VARCHAR(80) NOT NULL,
  arrival_city    VARCHAR(80) NOT NULL,
  departure_time  DATETIME     NOT NULL,
  arrival_time    DATETIME     NOT NULL,
  total_duration  VARCHAR(10)  NULL
);

USE railway;

CREATE TABLE trains (
  train_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  route_id          VARCHAR(50)  NOT NULL,
  train_type        VARCHAR(50)  NOT NULL,
  days_of_operation VARCHAR(50)  NOT NULL,
  CONSTRAINT fk_trains_route
    FOREIGN KEY (route_id) REFERENCES routes(route_id)
);
