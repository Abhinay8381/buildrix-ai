# Buildrix AI 🚀

**Buildrix AI** is an AI-powered web application generator and deployment platform built with **Spring Boot 4**, **Java 25**, and **Spring AI**. It enables developers and users to prompt, build, render, and deploy full-stack web applications dynamically with live Kubernetes-backed preview environments.

---

## 🏗️ Architecture Overview

The system is designed with a **Modular Monolith First** strategy, laying down strict domain boundaries to seamlessly transition into a **Microservices Architecture** (API Gateway, Intelligence Service, Workspace Service, Execution Service, Chat Service).

```
                      +-----------------------------+
                      |     Spring Cloud Gateway    |
                      +--------------+--------------+
                                     |
               +---------------------+---------------------+
               |                                           |
+--------------v--------------+             +--------------v--------------+
|     Workspace & Auth Service |             |     Intelligence Service    |
|   (JWT, Projects, Members)   |             | (Spring AI, LLM Tools, RAG) |
+--------------+--------------+             +--------------+--------------+
               |                                           |
+--------------v--------------+             +--------------v--------------+
|      Billing & Stripe       |             |  Qdrant Vector DB / MinIO   |
|   (Webhooks, Subscriptions) |             +--------------+--------------+
+-----------------------------+                            |
                                            +--------------v--------------+
                                            |  Kubernetes Preview Pods    |
                                            | (Dynamic Ingress & Execution)|
                                            +-----------------------------+
```

---

## 🌟 Core System Features

### 🔐 1. Authentication & Security
- Stateless **JWT Authentication** (`JwtAuthFilter`) with custom principal context (`JwtUserPrincipal`).
- Role-Based Access Control (RBAC) supporting `OWNER`, `EDITOR`, `VIEWER`.
- Secure Password Hashing with BCrypt & Spring Security Filter Chains.

### 📁 2. Project & Workspace Management
- Multitenant project creation & ownership control.
- Project membership, roles, permissions, and soft-delete handling.
- Multi-file code management structure mapped to MinIO storage key instances.

### 💳 3. Billing & Subscription Management (Stripe Integration)
- **Stripe Checkout API Integration** for handling subscription plans dynamically.
- **Event-Driven Webhook Router (`StripeEventRouter`)**: Clean implementation of the **Strategy Pattern** for processing asynchronous Stripe webhooks:
  - `checkout.session.completed`
  - `invoice.paid`
  - `customer.subscription.created` / `customer.subscription.updated` / `customer.subscription.deleted`
  - `invoice.payment_failed`
- **Idempotent Out-of-Order Webhook Processing**: Fallback lookup mechanisms to handle Stripe events arriving out of chronological order.

### ⚙️ 4. AI & Infrastructure (In Development Roadmap)
- **Spring AI & RAG Engine**: Integration with Qdrant Vector DB for codebase context ingestion.
- **Live Kubernetes Preview Execution**: Dynamic creation of Kubernetes Namespaces & Pods for running Vite/React previews.

---

## 📊 Database Schema (ER Diagram)

The underlying relational schema handles Users, Projects, Members, Subscriptions, Plans, Usage Logs, Chat Sessions, Messages, Files, and Kubernetes Previews:

```
[User] <--- (1:N) ---> [Project] <--- (1:N) ---> [ProjectFile]
  |                       |                       |
  | (1:N)                 | (1:N)                 | (1:N)
  v                       v                       v
[Subscription] <---> [ProjectMember]         [Preview (K8s)]
  |                       |
  v                       v
[Plan]               [ChatSession] <---> [ChatMessage]
```

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Language & Runtime** | Java 25 (Virtual Threads Enabled) |
| **Framework** | Spring Boot 4.x, Spring Web, Spring Security |
| **AI Integration** | Spring AI, Qdrant Vector DB |
| **Database & Persistence** | PostgreSQL 18, Spring Data JPA, Hibernate 7, MapStruct |
| **Object Storage** | MinIO Storage |
| **Payments** | Stripe Java SDK (Checkout & Webhooks) |
| **Container Orchestration**| Kubernetes (Dynamic Pod Execution & Ingress) |
| **Build Tool** | Apache Maven |

---

## 🚀 Getting Started

### Prerequisites
- Java 25 JDK
- PostgreSQL 18
- Docker / Kubernetes (Minikube / k3s)
- Stripe CLI (for testing webhooks)

### Setup & Run
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Abhinay8381/buildrix-ai.git
   cd buildrix-ai
   ```

2. **Configure Application Properties:**
   Ensure environment variables or `application.yaml` credentials are set for Database, JWT secret, and Stripe API keys:
   ```yaml
   stripe:
     api:
       secret: ${STRIPE_API_SECRET}
     webhook:
       secret: ${STRIPE_WEBHOOK_SECRET}
   ```

3. **Build & Run:**
   ```bash
   mvn clean package -DskipTests
   mvn spring-boot:run
   ```

---

## 📌 Status & Progress

- [x] **Week 1 Foundation**: Project Setup, Base Entity Audit Tracking, JPA Repositories.
- [x] **Authentication & Security**: Custom JWT Filter & Security Context.
- [x] **Project Workspace Domain**: Project creation, membership management, soft deletes.
- [x] **Stripe Billing Integration**: Checkout Session integration, Webhook Strategy Router, Idempotent Subscription Lifecycle.
- [ ] **Spring AI Integration**: Context retrieval (RAG) and tool calling.
- [ ] **Kubernetes Execution Engine**: Namespace allocation & live pod deployment for previews.

---

## 📄 License
Distributed under the MIT License.
