<img src="https://github.com/aunghein-dev/frontend_stocksManagement/blob/main/public/onlylogo.png?raw=true" alt="Openware Logo" width="111" align="right" hspace="0" />

# Openware Backend

![Language](https://img.shields.io/badge/language-Java-blue)
![Framework](https://img.shields.io/badge/framework-Spring%20Boot-brightgreen)
![Security](https://img.shields.io/badge/security-Spring%20Security-red)
![Database](https://img.shields.io/badge/database-PostgreSQL-316192)
![ORM](https://img.shields.io/badge/ORM-Hibernate-59666C)
![Validation](https://img.shields.io/badge/validation-Spring%20Validation-orange)
![GeoIP](https://img.shields.io/badge/geoip-MaxMind-0A66C2)
![API](https://img.shields.io/badge/API-REST%20%26%20WebFlux-00A6FF)
![JWT](https://img.shields.io/badge/auth-JWT-yellow)
![Build](https://img.shields.io/badge/build-Maven-C71A36)
![Status](https://img.shields.io/badge/status-active-brightgreen)

This is the **Spring Boot backend** for **Openware ERP / POS**.  
It powers the system with robust APIs, business logic, and persistence.

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

* **Spring Boot**
* **Java (JDK 17+)**
* **PostgreSQL**
* **Spring Data JPA**
* **Hibernate**
* **Spring Security**
* **JJWT**
* **Lombok**
* **Maven** or **Gradle**

---

## Architecture

The backend is a **Spring Boot** application that exposes **RESTful APIs** to the frontend. It follows a layered architecture:

* **Controller Layer**
* **Service Layer**
* **Repository Layer**

### Multi-Tenancy Implementation

This backend uses a **single-database, discriminator-based multi-tenancy** approach:

* Frontend authenticates via Supabase and receives a JWT.
* JWT includes a custom claim `account_id`.
* Backend extracts `account_id` from JWT.
* All queries are filtered by `account_id`.

---

## Getting Started

### Prerequisites

* **Java 17+**
* **Maven** or **Gradle**
* **PostgreSQL**
* **Supabase Project** with authentication configured

### Installation

1. **Clone the repository:**

```bash
git clone https://github.com/aunghein-dev/backend_stocksManagement.git
cd backend_stocksManagement
```

2. **Build the project:**

```bash
# Maven:
mvn clean install

# OR Gradle:
./gradlew clean build
```

### Configuration

#### Database Configuration

Create a PostgreSQL database:

```sql
CREATE DATABASE openware_stock_manager_backend;
```

Then configure `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/openware_stock_manager_backend
spring.datasource.username=your_pg_username
spring.datasource.password=your_pg_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

#### Supabase JWT Secret

Get this from **Supabase** → Project Settings → API → JWT Secret:

```properties
app.jwt.supabase-secret=YOUR_SUPABASE_PROJECT_JWT_SECRET
```

Do **not** hardcode in production—use environment variables.

#### CORS Configuration

```properties
app.cors.allowed-origins=http://localhost:3000,https://your-frontend-domain.com
```

### Running the Application

```bash
# Maven:
mvn spring-boot:run

# OR Gradle:
./gradlew bootRun
```

---

## API Endpoints

Documentation coming soon or available via Swagger if integrated.

---

## Deployment

Deploy using your preferred method:

* Heroku
* DigitalOcean
* AWS EC2 / ECS
* Google Cloud Run
* Kubernetes Cluster

Ensure proper environment setup and security configurations.

---

## Contributing

1. Fork the repo
2. Create a branch: `git checkout -b feature/your-feature`
3. Make your changes and test
4. Commit: `git commit -m "feat: your change"`
5. Push: `git push origin feature/your-feature`
6. Open a Pull Request

---

## License

MIT License – see the [LICENSE](LICENSE) file

---

## Contact

- 📧 Email: [aunghein.mailer@gmail.com](mailto:aunghein.mailer@gmail.com)
- 🌐 Website: [https://app.openwaremyanmar.site](https://app.openwaremyanmar.site)
- 🐛 GitHub Issues: [Open an Issue](https://github.com/aunghein-dev/backend_stocksManagement/issues)