# 🐾 PET HUB — Modern Pet E-Commerce Platform

[![Java](https://img.shields.io/badge/Java-17%20%7C%2021%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)](https://spring.io/projects/spring-security)
[![JWT](https://img.shields.io/badge/JWT-0.12.6-black?style=for-the-badge&logo=JSON%20web%20tokens)](https://jwt.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

> **PET HUB** is a full-featured, production-style, pet-focused e-commerce web application built from the ground up using **Java**, **Spring Boot 3**, **Spring Data JPA**, **Hibernate**, **Spring Security (Stateless JWT)**, **MySQL**, and a responsive modern **HTML5 / CSS3 / Vanilla JavaScript** storefront & admin suite.

---

## 📑 Table of Contents

1. [Project Overview](#-project-overview)
2. [Key Features](#-key-features)
   - [Customer Experience](#-customer-experience)
   - [Admin Control Center](#-admin-control-center)
3. [Technology Stack](#-technology-stack)
4. [Project Structure](#-project-structure)
5. [Demo Credentials](#-demo-credentials)
6. [Getting Started & Installation](#-getting-started--installation)
   - [Option A: 1-Click Dev Mode (H2 In-Memory)](#option-a-1-click-quickstart-dev-profile--h2-db)
   - [Option B: Production MySQL Mode](#option-b-mysql-database-mode)
7. [REST API Documentation](#-rest-api-documentation)
8. [Security & Business Logic Rules](#-security--business-logic-rules)
9. [Testing & Verification](#-testing--verification)

---

## 📌 Project Overview

PET HUB is tailored specifically for pet owners to discover, filter, and buy veterinarian-recommended pet food, engaging toys, grooming kits, health supplements, and accessories for various pet categories (Dogs, Cats, Birds, Fish & Aquatics, Small Animals).

The system enforces authoritative server-side pricing, inventory validation, historical price freezing in order line-items, atomic checkout operations (`@Transactional`), and role-based access control (RBAC).

---

## ✨ Key Features

### 🐶 Customer Experience
- **Authentication & Security:** Registration with input validation, BCrypt password hashing, and stateless JWT token authentication.
- **Product Catalog Discovery:**
  - Dynamic keyword search with real-time response.
  - Multi-faceted sidebar filtering by Pet Category, Product Category, Brand, Price range, and In-Stock availability.
  - Multi-criteria sorting (Price Low to High, Price High to Low, Name A-Z, Newest Arrivals).
  - Paged product grid with responsive design and instant visual feedback.
- **Rich Product Details:**
  - Interactive multi-image gallery with thumbnail switching.
  - Dynamic stock status indicator (`In Stock`, `Low Stock Alert`, `Out of Stock`).
  - Verified customer ratings (1–5 stars) and review history.
- **Dynamic Shopping Cart:**
  - Add to cart with single-click quick-add or quantity selectors.
  - Real-time server-side quantity increments, decrements, and item removals.
  - Accurate subtotal, automated tax calculations (5% GST), and free shipping threshold calculation.
- **Seamless 2-Step Checkout:**
  - Saved address selector with default address toggling.
  - Inline "Add New Shipping Address" modal form with validation.
  - Multiple payment options (Cash on Delivery & Digital Payments).
  - Atomic order processing with automated inventory deduction.
- **Order Management & Invoicing:**
  - Complete customer order history with status tracking badges (`PLACED`, `CONFIRMED`, `PACKED`, `SHIPPED`, `DELIVERED`, `CANCELLED`).
  - Order cancellation with automatic inventory restock.
- **Profile & Address Book:**
  - Update first name, last name, phone number, and change passwords securely.
  - Add, edit, remove, and assign default delivery addresses.

### ⚙️ Admin Control Center
- **Analytics Dashboard:** Live statistics on Gross Revenue, Total Orders, Pending Orders, Low Stock Alerts, and Total Users.
- **Product Management:** Full CRUD operations, SKU uniqueness verification, image uploading, discount pricing, stock count management, and active/inactive status toggles.
- **Category Taxonomy:** Create and edit Pet Categories and Product Categories with descriptions and thumbnail imagery.
- **Order Fulfillment:** Comprehensive view of all platform orders, inspect customer shipping details and ordered line items, and transition order lifecycle statuses.
- **Customer Directory:** View registered customers and manage account active/disabled states.
- **Review Moderation:** Global product review feed with capability to delete inappropriate or spam reviews.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend Core** | Java 17 / 21 / 24, Spring Boot 3.3.5, Spring MVC |
| **Data & Persistence** | Spring Data JPA, Hibernate ORM, MySQL Connector/J |
| **Database** | MySQL 8.0+ (Production) / H2 In-Memory (Development) |
| **Security & Auth** | Spring Security 6, JJWT (Java JWT `0.12.6`), BCrypt Password Encoder |
| **Frontend UI/UX** | Semantic HTML5, Modern CSS3 (CSS Variables, Flexbox, Grid), Vanilla JavaScript (ES6 Modules & Fetch API) |
| **Testing** | JUnit 5, Mockito, Spring Boot Test, Spring Security Test |
| **Build & Tooling** | Apache Maven 3.9+, Maven Wrapper (`mvnw` / `mvnw.cmd`) |

---

## 📂 Project Structure

```
pet-hub/
├── .tools/                               # Portable build tools (ignored from git)
├── database/
│   └── pet_hub.sql                       # Complete MySQL schema & initial sample seed data
├── src/
│   ├── main/
│   │   ├── java/com/pethub/
│   │   │   ├── config/                   # WebMvc & App configurations
│   │   │   ├── controller/               # REST API Controllers (Product, Cart, Order, Admin, etc.)
│   │   │   ├── dto/                      # Request & Response Data Transfer Objects
│   │   │   ├── entity/                   # JPA Domain Entities (User, Product, Order, Cart, etc.)
│   │   │   ├── exception/                # Custom Exceptions & Global Exception Handler
│   │   │   ├── mapper/                   # Entity-to-DTO conversion mappers
│   │   │   ├── repository/               # Spring Data JPA Repositories
│   │   │   ├── security/                 # JWT Filters, Token Provider, UserDetailsService
│   │   │   └── service/                  # Business Logic Interfaces & Implementations
│   │   └── resources/
│   │       ├── static/                   # Frontend Web Assets
│   │       │   ├── css/                  # Stylesheets (style, product, cart, checkout, admin, responsive)
│   │       │   ├── js/                   # JS Modules (api, auth, products, cart, checkout, profile, admin)
│   │       │   ├── admin/                # Admin Console HTML pages
│   │       │   ├── index.html            # Storefront Home Page
│   │       │   ├── products.html         # Catalog & Search Page
│   │       │   ├── product-details.html  # Single Product Deep Dive
│   │       │   ├── cart.html             # Shopping Cart
│   │       │   ├── checkout.html         # Multi-Step Checkout
│   │       │   ├── orders.html           # Customer Order History
│   │       │   ├── profile.html          # User Account & Address Book
│   │       │   ├── login.html            # Customer / Admin Login
│   │       │   └── register.html         # Customer Registration
│   │       ├── application.properties    # MySQL Production Configuration
│   │       └── application-dev.properties# H2 In-Memory Development Configuration
│   └── test/                             # Automated Unit & Integration Tests (15+ Tests)
├── .gitignore
├── mvnw.cmd                              # Maven Wrapper for Windows
├── pom.xml                               # Project dependencies and plugins
├── README.md                             # Project documentation
├── run-dev.bat                           # 1-Click launcher for H2 Dev mode
└── run-mysql.bat                         # 1-Click launcher for MySQL mode
```

---

## 🔐 Demo Credentials

The database auto-seeds ready-to-test accounts upon initialization:

| Account Type | Email | Password | Role / Access |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@pethub.com` | `Admin@123` | Full Storefront & Admin Portal (`ROLE_ADMIN`) |
| **Customer** | `customer@pethub.com` | `User@123` | Shopping, Cart, Orders, Profile (`ROLE_USER`) |

---

## 🚀 Getting Started & Installation

### Prerequisites
- **Java JDK 17** or higher installed ([Download JDK](https://adoptium.net/))
- **Git** installed on your system

---

### Option A: 1-Click Quickstart (Dev Profile / H2 DB)
The development profile runs with an **embedded in-memory H2 database** and seeds sample categories, products, and users automatically. No MySQL installation required!

#### On Windows:
Double-click `run-dev.bat` or execute in PowerShell:
```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

#### On Linux / macOS:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

### Option B: MySQL Database Mode

#### 1. Setup MySQL Database
Open your MySQL client or CLI:
```sql
CREATE DATABASE pet_hub;
```
Import the SQL script located at `database/pet_hub.sql`:
```bash
mysql -u root -p pet_hub < database/pet_hub.sql
```

#### 2. Configure Credentials
Check `src/main/resources/application.properties` and verify your username/password:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pet_hub?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

#### 3. Run the Application
```powershell
.\mvnw.cmd spring-boot:run
```

---

### 🌐 Accessing the Application

Once launched, navigate to:
- **Storefront:** [http://localhost:8080](http://localhost:8080)
- **Product Catalog:** [http://localhost:8080/products.html](http://localhost:8080/products.html)
- **Admin Dashboard:** [http://localhost:8080/admin/dashboard.html](http://localhost:8080/admin/dashboard.html)
- **Login Portal:** [http://localhost:8080/login.html](http://localhost:8080/login.html)
- **H2 Console (Dev mode):** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) *(JDBC URL: `jdbc:h2:mem:pethubdb`, User: `sa`, Password: blank)*

---

## 📡 REST API Documentation

| Group | Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/auth/register` | Public | Register new customer account |
| **Auth** | `POST` | `/api/auth/login` | Public | Authenticate user & return JWT token |
| **Auth** | `GET` | `/api/auth/me` | User / Admin | Retrieve current authenticated user profile |
| **Products** | `GET` | `/api/products` | Public | Search, filter, sort & paginate products |
| **Products** | `GET` | `/api/products/featured` | Public | Get curated featured products |
| **Products** | `GET` | `/api/products/{id}` | Public | Get single product deep-dive details |
| **Products** | `GET` | `/api/products/brands` | Public | Get distinct list of all product brands |
| **Reviews** | `GET` | `/api/products/{id}/reviews` | Public | Get customer reviews for a product |
| **Reviews** | `POST` | `/api/products/{id}/reviews` | User | Submit rating & review for product |
| **Cart** | `GET` | `/api/cart` | User | Get current user's cart summary & items |
| **Cart** | `POST` | `/api/cart/items` | User | Add product to shopping cart |
| **Cart** | `PUT` | `/api/cart/items/{itemId}?quantity={q}` | User | Update quantity of a cart item |
| **Cart** | `DELETE` | `/api/cart/items/{itemId}` | User | Remove specific item from cart |
| **Cart** | `DELETE` | `/api/cart` | User | Clear entire shopping cart |
| **Orders** | `POST` | `/api/orders` | User | Atomic place order from cart (`@Transactional`) |
| **Orders** | `GET` | `/api/orders` | User | Get paginated order history of user |
| **Orders** | `GET` | `/api/orders/{id}` | User | Get details and line-items of order |
| **Orders** | `PUT` | `/api/orders/{id}/cancel` | User | Cancel order & auto-restock inventory |
| **User** | `PUT` | `/api/users/profile` | User | Update personal details (name, phone) |
| **User** | `PUT` | `/api/users/change-password` | User | Securely change user password |
| **User** | `GET` | `/api/users/addresses` | User | Get all saved shipping addresses |
| **User** | `POST` | `/api/users/addresses` | User | Add new shipping address |
| **User** | `PUT` | `/api/users/addresses/{id}/default` | User | Set an address as primary default |
| **Admin** | `GET` | `/api/admin/dashboard` | Admin | Get analytics metrics & low stock alerts |
| **Admin** | `POST` | `/api/admin/products` | Admin | Create product with SKU, stock & prices |
| **Admin** | `PUT` | `/api/admin/products/{id}` | Admin | Update existing product details |
| **Admin** | `DELETE`| `/api/admin/products/{id}` | Admin | Delete / deactivate product |
| **Admin** | `POST` | `/api/admin/products/upload-image`| Admin | Upload image to server storage |
| **Admin** | `GET` | `/api/admin/orders` | Admin | Get all system orders |
| **Admin** | `PUT` | `/api/admin/orders/{id}/status` | Admin | Update order fulfillment status |
| **Admin** | `GET` | `/api/admin/users` | Admin | Customer management list |
| **Admin** | `PUT` | `/api/admin/users/{id}/status` | Admin | Enable / disable user account |
| **Admin** | `DELETE`| `/api/admin/reviews/{id}` | Admin | Moderate / remove reviews |

---

## 🔒 Security & Business Logic Rules

1. **Stateless JWT Flow:** Client requests include `Authorization: Bearer <token>`. User authentication context is restored per-request via `JwtAuthenticationFilter`.
2. **Role-Based Authorization:** Endpoints under `/api/admin/**` are strictly restricted to `ROLE_ADMIN`. Protected customer operations require `ROLE_USER` or `ROLE_ADMIN`.
3. **Atomic Stock & Order Management:** Order placement executes in a `@Transactional` block. Stock is verified and deducted immediately; if any item is out of stock, the entire transaction rolls back.
4. **Historical Price Guarantee:** At the time an order is placed, unit prices are frozen in the `order_items` table to protect financial history from future price adjustments.

---

## 🧪 Testing & Verification

Run the comprehensive unit and integration test suite:

```powershell
.\mvnw.cmd test
```

### Test Coverage Highlights:
- **`AuthControllerTest`:** User registration, password hashing, validation, and JWT login flows.
- **`ProductServiceTest`:** Product lookup, inventory queries, filtering, and exception triggers.
- **`CartServiceTest`:** Cart calculations, quantity changes, and price snapshot logic.
- **`OrderServiceTest`:** Atomic order placement, inventory deductions, stock clearance, and order cancellation.
- **`SecurityAccessTest`:** Endpoint security verification ensuring unauthenticated users cannot access protected cart, order, or admin endpoints.

---

## 👥 Authors & Acknowledgments

- **PET HUB Engineering Team**
- Built with Spring Boot, Java, and modern web standards.
