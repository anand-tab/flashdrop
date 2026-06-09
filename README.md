<div align="center">

# ⚡ FlashDrop

### Distributed Inventory & Order Orchestration for High-Traffic Flash Sales

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)

*A backend system that simulates how large-scale e-commerce platforms handle inventory, orders, and asynchronous stock deduction during peak traffic — without overselling.*

</div>

---

## 🧩 The Problem

During flash sales, thousands of users attempt to purchase the same product simultaneously. A naive implementation leads to:

| Problem | Impact |
|---|---|
| ❌ Overselling inventory | Fulfillment failures & customer complaints |
| ❌ Database contention | Slowdowns under load |
| ❌ Race conditions | Inconsistent stock data |
| ❌ Tight service coupling | Fragile, hard-to-scale architecture |

FlashDrop solves this with **event-driven architecture**, **asynchronous processing**, and **microservice decoupling**.

---

## 🏗️ System Architecture

<img width="1156" height="848" alt="Screenshot 2026-06-10 at 2 29 42 AM" src="https://github.com/user-attachments/assets/654d2e4e-0f86-49a6-aabc-338fef8db8e4" />


---

## 📦 Services

### 🛒 Order Service
- Accepts incoming purchase requests
- Creates and persists orders
- Publishes order events to Kafka
- Fully decoupled from inventory logic

### 📦 Inventory Service
- Manages products, categories, and SKUs
- Consumes Kafka events and deducts stock asynchronously
- Maintains complete inventory transaction history
- Supports hierarchical category structures

---

## 🗃️ Inventory Domain Model

```
Category  (hierarchical, self-referencing)
  └── Product  (common product info)
        └── SKU  (sellable variant, e.g. TS001-BLK-M)
              ├── Inventory         (current stock levels)
              └── Inventory Transaction  (full audit trail)
```

**Example hierarchy:**
```
Fashion
 └── Men
      └── T-Shirts
           └── Nike Round Neck T-Shirt
                 ├── TS001-BLK-M  →  Stock: 42
                 ├── TS001-BLK-L  →  Stock: 18
                 └── TS001-WHT-M  →  Stock: 55
```

**Transaction types:** `STOCK_IN` · `STOCK_OUT` · `RETURN` · `DAMAGED`

---

## 🔄 Event-Driven Flow

When a user places an order:

```
1. Order Service   →   creates and saves the order
2. Kafka           →   receives the published OrderPlacedEvent
3. Inventory Svc   →   consumes the event
4. Stock           →   deducted atomically at the SKU level
5. Transaction     →   recorded for audit trail
```

**Sample Kafka event payload:**
```json
{
  "orderId": "ORD123",
  "userId": 101,
  "skuId": 1,
  "quantity": 2
}
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot, Spring Data JPA, Spring Kafka |
| Database | PostgreSQL |
| Messaging | Apache Kafka |
| Build | Maven |
| Containerization | Docker *(planned)* |

---

## ✅ Features

- [x] Event-driven order → inventory pipeline
- [x] SKU-level inventory tracking
- [x] Hierarchical category management
- [x] Inventory audit trail with transaction history
- [x] Kafka-based asynchronous stock deduction
- [x] Extensible microservice architecture

---

## 🗺️ Roadmap

- [ ] Redis-based stock reservation (pre-checkout locking)
- [ ] Distributed locking to prevent race conditions
- [ ] API Gateway & Service Discovery
- [ ] Docker Compose setup
- [ ] Kubernetes deployment
- [ ] Monitoring with Prometheus & Grafana
- [ ] Saga pattern for order workflow
- [ ] Dead Letter Queue (DLQ) for failed events

---

## 🎯 What This Project Explores

FlashDrop is a hands-on study of real-world backend engineering concepts:

- **Distributed systems** — microservice coordination at scale
- **Event-driven architecture** — loose coupling via Kafka
- **Inventory management** — SKU-level tracking with full audit trail
- **High-concurrency** — async deduction to handle burst traffic
- **Scalable design** — built with horizontal scaling in mind

---

## 🤝 Contributing

Contributions, suggestions, and discussions are welcome!
Feel free to open an issue or submit a pull request.

If you find this project useful, please consider giving it a ⭐ — it helps others discover it!

---

<div align="center">
  <sub>Built to explore how real e-commerce backends survive flash sale chaos ⚡</sub>
</div>
