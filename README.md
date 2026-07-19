# LogiTrack API

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-007396?style=for-the-badge&logo=java&logoColor=white)](https://oracle.com/java/)
[![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

LogiTrack is an enterprise-grade backend REST API engineered to streamline modern supply chain management. The architecture is intentionally designed to unify two core logistics components: a **WMS (Warehouse Management System)** for precise inventory control, and a **TMS (Transportation Management System)** for fleet logistics and route distribution.

This project is built following strict software engineering standards, targeting high scalability, clean domain isolation, and production-ready code structures required by the European technology market.

---

# 🛠️ Enterprise Architecture & Tech Stack

The system leverages a modern technical stack aligned with European market demands for backend infrastructure:

* **Java 21 (LTS)** — Utilizing modern language features, strong type safety, and preparation for high-concurrency workloads.
* **Spring Boot 3.x** — Industry-standard framework for microservices and cloud-native applications.
* **Domain-Driven Design (DDD)** — Rich Domain Model architecture ensuring business rules remain decoupled from framework infrastructure.
* **Database & Migration** — Relational data persistence powered by Spring Data JPA with automated schema evolution via **Flyway Migrations** (PostgreSQL).
* **Testing Engine** — JUnit 5, AssertJ, and Mockito for isolated unit testing, alongside Spring Boot Integration Testing tools.

---

# 🚀 Advanced Resiliency & Data Integrity (Senior Engineering)

To survive production-level workloads and distributed environments, the application implements core enterprise integration patterns:

### 📥 Transactional Outbox Pattern
* **Guaranteed Event Delivery:** Avoids data loss from Dual-Write operations. When a domain event is triggered (e.g., `LowStockEvent`), it is serialized to JSON using a custom Jackson `ObjectMapper` configuration and persisted within the *same database transaction* as the business state change.
* **Resilient Polling:** An asynchronous background `OutboxProcessor` polls the database to safely process and dispatch pending events, ensuring *at-least-once delivery* guarantees even if external notification systems or message brokers suffer temporary outages.

### 🔒 Optimistic Concurrency Control (Locking)
* **Race Condition Prevention:** The `Product` entity is heavily shielded against concurrent inventory write collisions (e.g., simultaneous checkout requests drawing down the exact same stock item) using JPA `@Version` concurrency control.
* **Clean API Failures:** Concurrency collisions throw an `OptimisticLockingFailureException`, which is intercepted globally and translated into a semantic **HTTP 409 Conflict** response, preventing internal server errors (HTTP 500) and maintaining client-side data safety.

---

# 📦 System Capabilities & Modules

### 🏢 WMS Domain (Warehouse Management)
* **Rich Product Domain Model:** Core domain validations guarding structural constraints (SKU uniqueness, dimensions, cubic volume, and weight safety).
* **Automated Stock Threshold Alerts:** High-integrity `decreaseStock(int quantity)` operation enforcing business rules and automatically dispatching outbox events when stock breaches safety minimums.

### 🚛 TMS Domain (Transportation Management)
* **Fleet Capacity Guardrails:** Real-time volume (`m³`) and weight (`kg`) validation during shipping order assembly against physical `Vehicle` limitations.
* **Defensive Domain Entities:** Domain aggregates (`Vehicle`, `ShippingOrder`, `ShippingOrderItem`) protected against `NullPointerException` during unboxing and mapped with JPA-compliant reflection constructors.
* **Order Execution Lifecycle:** Multi-item shipping order calculation, status lifecycle transitions, and validation layers.

---

# 🧪 API Verification & Testing

The repository features comprehensive unit coverage alongside native HTTP client configurations for rapid execution.

## Running Test Suites

Execute unit and integration test suites via the Maven Wrapper:

```bash
./mvnw clean test
```

# 🔬 Stress & Integration Test Suite
* **ProductConcurrencyIT:** Advanced multi-threaded integration test executing parallel threads via `ExecutorService` and synchronized using a `CountDownLatch`. The suite empirically proves that the system rejects overlapping writes, throwing expected locking failures and preserving data integrity under high load.

* **ShippingOrderServiceTest** & **ShippingOrderTest**: Direct domain unit testing validating vehicle overload rejections, item addition math, and domain aggregate consistency without database overhead.

## IDE HTTP Client Testing
Access testing scripts for direct execution inside IntelliJ IDEA:
```Bash
src/main/java/com/logitrack/api/products-test.http
```
```Bash
src/main/java/com/logitrack/api/shipping-orders-test.http
```
```Bash
src/main/java/com/logitrack/api/vehicles-test.http
```


# 🔧 Installation & Deployment
## Prerequisites:

* **Java Development Kit (JDK) 21 or higher.**

* **PostgreSQL 16+** running locally on port 5432 with database logitrack_wms_tms.

## Step-by-Step Setup

1. ### Clone the repository:
```Bash
git clone [https://github.com/TessioCarvalho/logitrack-api.git](https://github.com/TessioCarvalho/logitrack-api.git)
cd logitrack-api
```
2. ### Run database migrations & build the application:
```Bash
./mvnw clean compile
```
3. ### Launch the application locally:
```Bash
./mvnw spring-boot:run
```

# 🗺️ Technical Roadmap
* [x] Data Persistence Integration via Spring Data JPA.

* [x] Database Schema Versioning with Flyway Migrations.

* [x] Automated Stock Threshold Alerts via Transactional Outbox.

* [x] Concurrency Control Real-Time Validation under multi-threaded stress.

* [x] Vehicle Fleet Capacity & Shipping Order Validation Core (TMS Module).

* [ ] Automated Route Optimization & Dispatch Status Management.