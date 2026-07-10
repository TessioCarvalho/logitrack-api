# LogiTrack API

LogiTrack is an enterprise-grade backend REST API engineered to streamline modern supply chain management. The architecture is intentionally designed to unify two core logistics components: a **WMS (Warehouse Management System)** for precise inventory control, and a **TMS (Transportation Management System)** for fleet logistics and route distribution.

This project is built following strict software engineering standards, targeting high scalability, clean domain isolation, and production-ready code structures required by the European technology market.

---

## 🛠️ Enterprise Architecture & Tech Stack

The system leverages a modern technical stack aligned with European market demands for backend infrastructure:

* **Java 21 (LTS)** - Utilizing modern language features, virtual threads ready, and strong type safety.
* **Spring Boot 3.x** - Industry-standard framework for microservices and cloud-native applications.
* **Domain-Driven Design (DDD)** - Clean separation of concerns, ensuring business rules remain decoupled from infrastructure.
* **RESTful Architecture** - Standardized, predictable API contracts with explicit request/response payloads.

---

## 🚀 System Capabilities & Modules

### WMS Domain (Warehouse Management)
* **Product Catalog:** Robust domain model capturing product metadata and structural attributes.
* *In Pipeline:* Dynamic stock allocation, real-time inventory validation, and warehouse zone optimization.

### TMS Domain (Transportation Management)
* *In Pipeline:* Fleet vehicle onboarding, volumetric payload tracking, and delivery route status management.

---

## 🧪 API Verification & Testing

The repository features native HTTP client configurations for seamless testing directly within the IDE (IntelliJ IDEA)[cite: 5]. You can access and execute testing scripts at:
`src/main/java/com/logitrack/api/products-test.http`

### Available Endpoints
* `GET /products` — Retrieves the current inventory catalog.
* `POST /products` — Registers a new product entity with semantic validation.

---

## 🔧 Installation & Deployment

### Prerequisites
* **Java Development Kit (JDK) 21** or higher.

### Step-by-Step Setup
1. Clone the repository:
   ```bash
   git clone [https://github.com/TessioCarvalho/logitrack-api.git](https://github.com/TessioCarvalho/logitrack-api.git)
   
2. Build and launch the application locally using the Maven Wrapper:
    ```bash
   ./mvnw spring-boot:run

## Technical Roadmap

[ ] Data Persistence Integration via Spring Data JPA.

[ ] Database Schema Versioning with Flyway Migrations.

[ ] Automated Stock Threshold Alerts (Safety Stock/Reorder Point).

[ ] Fleet Routing Engine Core (TMS Module).