<div align="center">

# ⚡ FlashDrop

### *Not every order is created equal — FlashDrop knows the difference*

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)

A distributed inventory and order orchestration platform that simulates how real e-commerce systems intelligently route inventory operations based on product demand — handling flash sale traffic without overselling, without crashing, and without slowing down.

</div>

---

## 💡 The Core Idea

Most inventory systems treat every order the same way:

```
Order → Inventory Service → Database
```

That works fine for 10 orders per second. It fails catastrophically for 10,000.

FlashDrop takes a different approach. It **classifies every product** and routes each order through the right path for its traffic level.

| | 🔥 Hot Product | ❄️ Cold Product |
|---|---|---|
| **What is it?** | Flash sale / limited edition item | Regular catalogue item |
| **Traffic** | Thousands of orders per second | Normal, predictable volume |
| **Examples** | iPhone launch, limited sneakers, festival sale | Water bottle, laptop bag, daily essentials |
| **How processed** | Redis → Kafka → Database (async) | Direct to Inventory → Database (sync) |

This single design decision is what separates a system that **survives** a flash sale from one that goes down.

---

## 🚨 The Problem FlashDrop Solves

Picture this: a new iPhone drops. 50,000 users hit "Buy Now" at the exact same second. A traditional system does this:

```
50,000 users
     ↓
Inventory Service  ← all 50,000 hammering simultaneously
     ↓
PostgreSQL         ← row-level locks, contention, timeouts
```

The result:

- **Overselling** — 3,000 units sold, only 500 in stock
- **Database meltdown** — lock contention brings everything down
- **Race conditions** — two threads read `stock = 1`, both sell it
- **Slow responses** — users wait 10+ seconds and rage-quit

FlashDrop prevents all of this.

---

## 🏗️ How It Works

<img width="1156" height="848" alt="Screenshot 2026-06-10 at 2 29 42 AM" src="https://github.com/user-attachments/assets/27dbcba2-82b5-4740-bd81-ccbb7e9bfd43" />


```
                         [ User ]
                            |
                            ▼
                     [ API Gateway ]
                            |
              ┌─────────────┼─────────────┐
              ▼             ▼             ▼
         [Auth Svc]   [User Svc]    [Product Svc]


                      [ Order Service ]
                            |
              Check Product in Redis Cache
              (EXISTS stock:<productId> ?)
            ┌───────────────┴───────────────┐
            │                               │
            YES                            NO
            │                               │
            │                               │
     🔥 HOT Product PATH            ❄️ COLD Product PATH
            │                               │
            ▼                               ▼
    [ Redis ]                    [ Inventory Service ]
    Atomic reservation                      │
            │                               ▼
            ▼                         [ PostgreSQL ]
    [ Kafka ]
    Async event
            │
            ▼
    [ Inventory Service ]
    Persist to DB
            │
            └──────────────┐
                           ▼
                  [ Kafka Notification ]
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
           Email          SMS        In-App
```

---

## 🔥 Hot Path — Flash Sale Products

When a user orders a hot product, **Redis handles the critical part** before any database is touched.

**Step 1 — Order arrives**
Order Service receives the request and checks: is this SKU marked HOT?

**Step 2 — Redis atomic reservation**
```
Current stock in Redis: 100

User A   →  DECRBY 1  →  99  ✓ Reserved
User B   →  DECRBY 1  →  98  ✓ Reserved
User C   →  DECRBY 1  →  97  ✓ Reserved
...
User 101 →  DECRBY 1  →  -1  ✗ REJECTED — out of stock
```

Redis processes these sequentially at memory speed. No race condition is possible. Overselling is mathematically impossible.

**Step 3 — Kafka event published**
Only after a successful reservation does the order flow forward:
```json
{
  "orderId": "ORD123",
  "userId": 101,
  "skuId": 42,
  "quantity": 2
}
```

**Step 4 — Inventory Service consumes asynchronously**
The database is updated in the background. The user already has their confirmation.

**Step 5 — Notification sent**
Email, SMS, and in-app notification fired via Kafka.

### Why this wins
- ✅ Redis handles burst traffic at microsecond speed
- ✅ Database is protected — writes happen async, not under load
- ✅ Zero overselling — atomic `DECRBY` is a single, indivisible operation
- ✅ TTL on reservations means abandoned carts auto-release stock

---

## ❄️ Cold Path — Regular Products

No Redis. No Kafka overhead. Just clean, direct processing.

```
Order Service
      │
      ▼
Inventory Service  (synchronous stock check + deduction)
      │
      ▼
PostgreSQL
      │
      ▼
Kafka Notification  →  Email / SMS / In-App
```

### Why this is the right call
- ✅ Simpler = fewer failure points
- ✅ Lower infrastructure cost
- ✅ Synchronous — user knows immediately if the order succeeded
- ✅ No Redis warming, no consumer lag concerns

---

## 🎯 Product Classification

Each product is tagged `HOT` or `COLD`. The Order Service reads this flag and routes accordingly.

| Product | Classification | Why |
|---|---|---|
| iPhone 18 Launch Edition | 🔥 HOT | Limited stock, massive spike expected |
| Limited Edition Air Jordan | 🔥 HOT | Sells out in seconds |
| Festival Sale Items | 🔥 HOT | Predictable traffic surge |
| Water Bottle | ❄️ COLD | Steady, low-volume demand |
| Laptop Bag | ❄️ COLD | No spike risk |
| Office Stationery | ❄️ COLD | Normal catalogue traffic |

---

## 📦 Services

### 🛒 Order Service
The brain of FlashDrop. Receives every order, decides HOT vs COLD, and routes accordingly.
- Accepts order requests
- Classifies product type (HOT / COLD)
- Triggers Redis reservation for hot products
- Publishes Kafka events

### 📦 Inventory Service
The worker. Manages all product data and processes stock updates.
- Product, Category, and SKU management
- Consumes Kafka events (hot path)
- Direct stock deduction (cold path)
- Full inventory transaction history (`STOCK_IN` · `STOCK_OUT` · `RETURN` · `DAMAGED`)

### ⚡ Redis
The gatekeeper for flash sales. Sits in front of the database to absorb burst traffic.
- Atomic stock reservation via `DECRBY`
- Oversell prevention at memory speed
- Reservation TTL — auto-releases stock on abandoned carts

### 📨 Kafka
The async backbone. Decouples services so a spike in orders doesn't spike the database.
- Inventory update events (hot path)
- Order confirmation events
- Notification fanout (Email · SMS · In-App)

### 🔐 Auth Service
Issues and validates JWT tokens. Every request through the API Gateway is authenticated.

### 👤 User Service
Manages user profiles and registration — wired to Auth Service for the sign-up flow.

---

## 🗃️ Inventory Domain Model

```
Category  (supports hierarchy — e.g. Fashion → Men → T-Shirts)
  └── Product  (e.g. Nike Round Neck T-Shirt)
        └── SKU  (e.g. TS001-BLK-M, TS001-BLK-L)
              ├── Inventory            →  current stock level
              └── InventoryTransaction →  full audit trail
                        ├── STOCK_IN
                        ├── STOCK_OUT
                        ├── RETURN
                        └── DAMAGED
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot · Spring Data JPA · Spring Kafka |
| Cache / Reservation | Redis |
| Database | PostgreSQL |
| Messaging | Apache Kafka |
| Build | Maven |
| Container | Docker *(planned)* |

---

## ✅ What's Built

- [x] Hot / Cold product classification and routing
- [x] Redis atomic stock reservation (`DECRBY` + oversell guard)
- [x] Kafka-based async inventory deduction (hot path)
- [x] Synchronous inventory deduction (cold path)
- [x] SKU-level inventory tracking
- [x] Full inventory transaction history
- [x] Hierarchical category management
- [x] Notification service (Email · SMS · In-App)
- [x] Auth Service with JWT
- [x] Microservice architecture with API Gateway

---

## 🗺️ What's Coming

- [ ] Reservation TTL expiry + stock restoration scheduler
- [ ] Redis Lua scripts for complex atomic operations
- [ ] Service Discovery
- [ ] Docker Compose setup
- [ ] Kubernetes deployment
- [ ] Distributed tracing
- [ ] Prometheus + Grafana monitoring

---

## 🎯 Engineering Concepts Demonstrated

FlashDrop explores how real backend systems at scale are designed:

- **Traffic-aware routing** — different processing paths based on product demand
- **Redis atomic operations** — `DECRBY` as an oversell-proof reservation primitive
- **Event-driven architecture** — loose coupling via Kafka
- **Async vs sync tradeoffs** — choosing the right model per use case
- **Inventory reservation pattern** — reserve first, persist later
- **Distributed systems design** — handling concurrency without database locks
- **SKU-level tracking** — granular stock management with full audit trail

---

## 🤝 Contributing

Contributions, ideas, and discussions are welcome. Open an issue or submit a PR.

If this project is useful or interesting to you, please give it a ⭐ — it helps others find it.

---

<div align="center">
  <sub>Built to understand what actually happens inside e-commerce systems when millions of people want the same thing at the same time ⚡</sub>
</div>
