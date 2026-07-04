# Enterprise Identity & Access Management (IAM) Service

A production-ready **Authentication & Identity Management (IAM)** application built with **Java 21**, **Spring Boot**, **React**, and **PostgreSQL**.

The project focuses on modern authentication, secure session management, production-ready architecture, and clean API design. It demonstrates backend engineering practices commonly used in enterprise applications.

---

## ✨ Features

### 🔐 Secure Authentication
- User Registration & Login
- JWT-based Authentication
- Access Token & Refresh Token support
- Secure logout
- Password hashing with Spring Security

### 🔄 Refresh Token Rotation
- Implemented a sliding-window refresh token strategy.
- Keeps users securely logged in while reducing the risk of session hijacking.

### 🛡️ Token Reuse Detection
- Detects when an old refresh token is reused.
- Automatically revokes the compromised session to prevent unauthorized access.

### 📋 Role-Based Authorization
- Role-based access using Spring Security.
- Resource-level authorization to ensure users can only access their own data.
- Administrator access for protected operations.

### 🧹 Automated Database Cleanup
- Scheduled cleanup using Spring's `@Scheduled`.
- Automatically removes expired refresh tokens.
- Keeps the database clean and efficient.

### ⚡ Database Optimization
- Added database indexes on frequently queried columns.
- Improved token lookup and cleanup performance.

### 📦 Standardized API Responses
- Global `RestResponse<T>` wrapper for all REST APIs.
- Consistent response structure across the application.

### 🌍 Environment Configuration
- Separate configurations for Development and Production.
- Spring Profiles (`application-dev.yml` and `application-prod.yml`).
- Sensitive values stored using environment variables.

### 📖 API Documentation
- Interactive API documentation using Swagger UI (Springdoc OpenAPI).

---

# 🏗️ Tech Stack

## Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- JWT
- PostgreSQL
- Maven
- Swagger (Springdoc OpenAPI)

## Frontend

- React 18
- Vite
- Tailwind CSS
- Axios

---

# 📁 Project Structure

```
backend/
├── authentication
├── authorization
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
├── service
└── scheduler

frontend/
├── components
├── pages
├── api
├── store
├── assets
└── lib
```

---

# 🔒 Security Highlights

- JWT Authentication
- Refresh Token Rotation
- Token Reuse Detection
- Secure Password Hashing
- HttpOnly Cookies
- Secure Cookies (Production)
- Role-Based Authorization
- Resource Ownership Validation
- Global Exception Handling
- Input Validation

---

# 🚀 Performance Improvements

- Database indexing for faster queries
- Scheduled cleanup of expired tokens
- Optimized database operations
- Consistent API response format
- Clean project architecture

---

# 📌 Future Improvements

- Docker & Docker Compose
- CI/CD Pipeline (GitHub Actions)
- Redis Caching
- Email Verification
- Password Reset
- Multi-Factor Authentication (MFA)
- OAuth2 (Google & GitHub Login)
- Kubernetes Deployment
- Monitoring with Prometheus & Grafana

---

# 📸 Screenshots

_Add screenshots here._

---

# 🚀 Getting Started

## Prerequisites

- Java 21
- Maven
- PostgreSQL
- Node.js
- npm

## Backend

```bash
git clone <repository-url>

cd backend

mvn clean install

mvn spring-boot:run
```

## Frontend

```bash
cd frontend

npm install

npm run dev
```

## 📄 About This Project

This project was built for learning, portfolio, and as a reusable production-ready authentication service. The goal is to use it as a foundation for future projects instead of rebuilding authentication and authorization from scratch each time.

## 👨‍💻 Author

Built with ❤️ using Java, Spring Boot, React, and PostgreSQL.