      
# Hotel Reservation Web Application

## Table of Contents

1.  [Overview](#overview)
2.  [Features](#features)
3.  [Tech Stack](#tech-stack)
4.  [Prerequisites](#prerequisites)
5.  [Setup and Configuration](#setup-and-configuration)
    *   [1. Database Setup](#1-database-setup)
    *   [2. Application Configuration](#2-application-configuration)
    *   [3. Project Setup](#3-project-setup)
6.  [Running the Application](#running-the-application)
    *   [Using an IDE](#using-an-ide)
    *   [Using Maven](#using-maven)
7.  [Accessing the Application](#accessing-the-application)
8.  [Project Structure](#project-structure)
9.  [File Uploads](#file-uploads)

## Overview

This is a simple hotel reservation web application built with Spring Boot. It allows users to register, log in, view available rooms, make reservations, and upload an ID card for their reservation.

## Features

*   User Registration (Sign Up)
*   User Login (Sign In) & Logout
*   View available hotel rooms
*   Search for rooms
*   Make a reservation
*   Upload an ID card (e.g., PDF, image) during reservation
*   View personal reservations

## Tech Stack

*   Java 17
*   Spring Boot 3.x
*   Spring MVC (Web)
*   Spring Data JPA (Persistence)
*   Hibernate (JPA Implementation)
*   Thymeleaf (Server-side Templating Engine)
*   MySQL (Database)
*   Maven (Build Tool)
*   H2 Database (For automated tests)

## Prerequisites

*   JDK 17 or higher installed and configured.
*   Apache Maven installed and configured.
*   MySQL Server installed and running.
*   An IDE like IntelliJ IDEA, Eclipse (with STS), or VS Code (with Java extensions) is recommended.

## Setup and Configuration

### 1. Database Setup

1.  **Ensure MySQL Server is running.**
2.  **Create the Database:**
    Connect to your MySQL server and execute the following SQL command to create the database:
    ```sql
    CREATE DATABASE IF NOT EXISTS hotel_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
3.  **(Optional) Create Tables Manually:**
    You can use the provided `schema.sql` (if available in the project, or generate one) to create the tables. Alternatively, with `spring.jpa.hibernate.ddl-auto=update`, Spring Boot will attempt to create/update tables if the database exists and your MySQL user has privileges.
    The required tables are: `users`, `rooms`, `reservations`.

### 2. Application Configuration

1.  Open the main application properties file: `src/main/resources/application.properties`.
2.  **Configure MySQL Connection:**
    Update the following properties with your MySQL database details:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/hotel_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
    spring.datasource.username=YOUR_MYSQL_USERNAME
    spring.datasource.password=YOUR_MYSQL_PASSWORD
    ```
    Replace `YOUR_MYSQL_USERNAME` and `YOUR_MYSQL_PASSWORD` with your actual credentials.
3.  **File Upload Directory:**
    The application will attempt to create an `uploads` directory in its root working directory for storing uploaded ID cards.
    ```properties
    file.upload-dir=./uploads/
    ```

### 3. Project Setup

1.  Clone or download this repository/project files.
2.  Open the project in your IDE as a Maven project.
3.  The IDE should automatically download dependencies defined in `pom.xml`. If not, trigger a Maven sync/reload.

## Running the Application

### Using an IDE (Recommended for Development)

1.  Locate the main application class: `HotelReservationAppApplication.java` (usually in a package like `com.example.hotelapp` or `com.example.hotel_reservation_app`).
2.  Right-click on it and select "Run" or "Debug".

### Using Maven

1.  Open a terminal or command prompt.
2.  Navigate to the root directory of the project (where `pom.xml` is located).
3.  Run the command:
    ```bash
    mvn spring-boot:run
    ```

## Accessing the Application

Once the application is running, open your web browser and navigate to:
`http://localhost:8080`

You should see the home page.

## Project Structure

```
hotel-reservation-app/
├── pom.xml
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/example/hotelapp/ (or hotel_reservation_app)
│ │ │ ├── HotelReservationAppApplication.java (Main class)
│ │ │ ├── config/ (WebConfig for static resources)
│ │ │ ├── controller/ (AuthController, ReservationController, RoomController)
│ │ │ ├── entity/ (User, Room, Reservation)
│ │ │ ├── repository/ (JPA Repositories)
│ │ │ └── service/ (Business logic services)
│ │ └── resources/
│ │ ├── application.properties (Main app config)
│ │ ├── application-test.properties (Test environment config - H2)
│ │ ├── static/
│ │ │ └── style.css
│ │ └── templates/ (Thymeleaf HTML templates)
│ │ ├── login.html
│ │ ├── register.html
│ │ ├── index.html
│ │ ├── rooms.html
│ │ ├── reservation_form.html
│ │ └── my_reservations.html
│ └── test/
│ ├── java/ (Test classes)
│ └── resources/ (Test specific resources)
└── uploads/ (Directory for uploaded files - created at runtime)
```
## File Uploads

*   Uploaded files (ID cards) are stored in the `uploads/` directory in the project's root (when run from IDE/Maven plugin).
*   The `uploads/` directory is configured in `application.properties` via `file.upload-dir`.
*   `WebConfig.java` configures Spring MVC to serve static files from this `uploads` directory.
