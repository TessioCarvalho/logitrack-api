# LogiTrack API

LogiTrack is an enterprise-grade backend REST API engineered to streamline modern supply chain management. The architecture is intentionally designed to unify two core logistics components: a **WMS (Warehouse Management System)** for precise inventory control, and a **TMS (Transportation Management System)** for fleet logistics and route distribution.

This project is built following strict software engineering standards, targeting high scalability, clean domain isolation, and production-ready code structures required by the European technology market.

---

## 🛠️ Enterprise Architecture & Tech Stack

The system leverages a modern technical stack aligned with European market demands for backend infrastructure:

* **Java 21 (LTS)** - Utilizing modern language features, virtual threads ready, and strong type safety.
* **Spring Boot 3.x** - Industry-standard framework for microservices and cloud-native applications.
* **Domain-Driven Design (DDD)** - Rich Domain Model architecture ensuring business rules remain decoupled from infrastructure infrastructure.
* **Database & Migration** - Relational data persistence powered by Spring Data JPA and automated schema evolution via **Flyway Migrations**.

---

## 🚀 Advanced Resiliency & Data Integrity (Senior Engineering)

To survive production-level workloads and distributed environments, the application implements core enterprise integration patterns:

### 📥 Transactional Outbox Pattern
* **Guaranteed Delivery:** Avoids data loss from Dual-Write operations. When a domain event is triggered, it is serialized to JSON using a custom Jackson ObjectMapper configuration and persisted within the *same database transaction* as the business state change.
* **Resilient Polling:** An asynchronous background `OutboxProcessor` polls the database to safely process and dispatch pending events, ensuring *at-least-once delivery* guarantees even if external notification systems or message brokers suffer temporary outages.

### 🔒 Optimistic Concurrency Control (Locking)
* **Race Condition Prevention:** The `Product` entity is heavily shielded against concurrent inventory write collisions (e.g., simultaneous checkout requests drawing down the exact same stock item) using JPA `@Version` concurrency control.
* **Clean API Failures:** Concurrency collisions throw an `OptimisticLockingFailureException`, which is intercepted globally and translated into a semantic **HTTP 409 Conflict** response, preventing internal server errors (500) and maintaining client-side data safety.

---

## 📦 System Capabilities & Modules

### WMS Domain (Warehouse Management)
* **Rich Product Domain Model:** Core domain validations guarding structural constraints (SKU uniqueness, dimensions, cubic volume, and weight safety).
* **Automated Stock Threshold Alerts:** High-integrity `decreaseStock(int quantity)` operation that enforces business rules and automatically dispatches a `LowStockEvent` when current levels breach configured safety minimums.

### TMS Domain (Transportation Management)
* *In Pipeline:* Shipping Order execution (`ShippingOrder` workflow), volumetric payload tracking, and delivery route status management.

---

## 🧪 API Verification & Testing

The repository features native HTTP client configurations for seamless testing directly within the IDE (IntelliJ IDEA). You can access and execute testing scripts at:
`src/main/java/com/logitrack/api/products-test.http`

### Available Endpoints
* `GET /products` — Retrieves the current inventory catalog.
* `POST /products` — Registers a new product entity with semantic validation.

### 🔬 High-Stress Test Suite
* **`ProductConcurrencyIT`:** Advanced multi-threaded integration test executing parallel threads via `ExecutorService` and synchronized using a `CountDownLatch` starting gun. The suite empirically proves that the system rejects overlapping writes, throwing expected locking failures and preserving data integrity.

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

## 🗺️ Technical Roadmap
[x] Data Persistence Integration via Spring Data JPA.

[x] Database Schema Versioning with Flyway Migrations.

[x] Automated Stock Threshold Alerts via Transactional Outbox.

[x] Concurrency Control Real-Time Validation under multi-threaded stress.

[ ] Fleet Routing Engine Core & Shipping Order Lifecycle (TMS Module).