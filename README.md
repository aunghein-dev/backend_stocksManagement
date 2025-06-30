<p align="center">
  <img src="public/onlylogo.png" alt="Openware Logo" width="180"/>
</p>

# Openware Stock Manager - Backend

This is the robust Spring Boot backend for the Openware Stock Manager, a modern Point-of-Sale (POS) inspired web application. It handles all business logic, data persistence, and API interactions, supporting a multi-tenant architecture with secure data isolation.

---

## Table of Contents

* [Features](#features)
* [Technologies Used](#technologies-used)
* [Architecture](#architecture)
* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Installation](#installation)
    * [Configuration](#configuration)
    * [Running the Application](#running-the-application)
* [API Endpoints](#api-endpoints)
* [Deployment](#deployment)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)

---

## Features

* **Core Business Logic:** Manages all operations related to products, inventory, orders, and users.
* **Secure API Endpoints:** Provides RESTful APIs for frontend communication, secured with JWT authentication.
* **Multi-Tenant Data Isolation:** Implements single database tenancy, ensuring each customer's data is securely isolated using a unique `account_id` derived from the JWT.
* **Robust Data Persistence:** Leverages **PostgreSQL** for reliable and scalable data storage.
* **Authentication Integration:** Validates JWTs issued by **Supabase**, ensuring secure user access.
* **Scalable & Performant:** Built with Spring Boot for high performance and easy scalability.
* **Comprehensive Error Handling:** Provides meaningful error responses for API consumers.

---

## Technologies Used

* **Spring Boot:** The framework for building stand-alone, production-grade Spring-based applications.
* **Java (JDK 17+):** The primary programming language.
* **PostgreSQL:** The powerful open-source relational database used for data storage.
* **Spring Data JPA:** Simplifies data access and persistence with relational databases.
* **Hibernate:** The JPA implementation for ORM.
* **Spring Security:** Handles authentication and authorization, including JWT validation.
* **JJWT:** A Java library for JSON Web Tokens.
* **Lombok:** Reduces boilerplate code (e.g., getters, setters, constructors).
* **Maven** or **Gradle:** Dependency management and build automation.

---

## Architecture

The backend is a **Spring Boot** application that exposes **RESTful APIs** to the frontend. It follows a layered architecture:

* **Controller Layer:** Handles incoming HTTP requests and delegates to the service layer.
* **Service Layer:** Contains the core business logic and orchestrates operations.
* **Repository Layer:** Interacts with the **PostgreSQL** database via Spring Data JPA.

### Multi-Tenancy Implementation

This backend uses a **single-database, multi-schema, or discriminator-based multi-tenancy** approach (typically discriminator, using `account_id`).
* Upon user authentication (managed by Supabase), the frontend receives a JWT.
* This JWT contains a custom claim for `account_id` (or similar tenant identifier).
* The Spring Boot backend extracts this `account_id` from the incoming JWT.
* All database queries are dynamically filtered using this `account_id`, ensuring that each tenant can only access their own data, even though all tenants share the same PostgreSQL database instance.

---

## Getting Started

Follow these steps to get the backend up and running on your local machine.

### Prerequisites

* **Java Development Kit (JDK) 17 or higher**
* **Maven** (preferred) or **Gradle**
* **PostgreSQL** database server running
* A **Supabase Project** with authentication configured, from which you can obtain your **Supabase JWT Secret**.

### Installation

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/aunghein-dev/backend_stocksManagement.git](https://github.com/aunghein-dev/backend_stocksManagement.git)
    cd backend_stocksManagement
    ```

2.  **Build the project:**
    ```bash
    # If using Maven:
    mvn clean install
    # If using Gradle:
    ./gradlew clean build
    ```

### Configuration

1.  **Database Configuration:**
    Create a PostgreSQL database for the application.
    ```sql
    CREATE DATABASE openware_stock_manager_backend;
    ```
    Update `src/main/resources/application.properties` (or `application.yml`) with your PostgreSQL connection details:

    ```properties
    # application.properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/openware_stock_manager_backend
    spring.datasource.username=your_pg_username
    spring.datasource.password=your_pg_password
    spring.jpa.hibernate.ddl-auto=update # Use 'create' for first run, then 'update' or 'none' for production
    spring.jpa.show-sql=true
    ```

2.  **Supabase JWT Secret:**
    The backend needs the same JWT secret that your Supabase project uses to sign its authentication tokens. This is crucial for verifying the authenticity and integrity of JWTs received from the frontend.

    * Go to your Supabase project settings -> API.
    * Find the "JWT Secret" (or "Anon Key" for some configurations, but usually a specific JWT Secret is available).
    * Add this secret to your `application.properties`:

    ```properties
    # application.properties
    app.jwt.supabase-secret=YOUR_SUPABASE_PROJECT_JWT_SECRET_HERE
    # This should be a strong, complex secret. Do NOT hardcode in production. Use environment variables!
    ```

3.  **CORS Configuration:**
    Configure CORS to allow requests from your frontend application's origin.

    ```properties
    # application.properties
    app.cors.allowed-origins=http://localhost:3000,[https://your-frontend-domain.com](https://your-frontend-domain.com)
    ```

### Running the Application

Once configured, run the Spring Boot application:

```bash
# If using Maven:
mvn spring-boot:run
# If using Gradle:
./gradlew bootRun
