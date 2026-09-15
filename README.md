# Buildrix AI 🚀

**Buildrix AI** is an AI-powered web application generator and deployment platform built with **Spring Boot 4**, **Java 25**, and **Spring AI**. It enables users to prompt, generate, edit, and preview interactive **React / Vite** applications dynamically with live **Kubernetes-backed runner environments**.

Microservices Repo - https://github.com/Abhinay8381/distributed-buildrix-ai
Demo Video - https://drive.google.com/file/d/1ITHYR_bLwGPOBN07FNfvwHGQEIcSh7A8/view?usp=sharing

---

## 🏗️ Architecture Overview

The system follows a **Monolith First** design approach, with decoupled domain modules ready to evolve into microservices:

```
                                  +------------------------------------+
                                  |     Buildrix React Frontend        |
                                  |   (Editor, Chat, Preview Iframe)   |
                                  +-----------------+------------------+
                                                    |
                                     +--------------+--------------+
                                     |                             |
                      +--------------v--------------+ +------------v----------------+
                      |   Buildrix AI Main Backend  | | Buildrix Proxy Service      |
                      |   (Spring Boot - Port 8080) | | (Reverse Proxy - Port 8090)  |
                      +--------------+--------------+ +------------+----------------+
                                     |                             |
     +-------------------------------+-------------------+         | Reads Route IP
     |                               |                   |         v
+----v-------+               +-------v-------+   +-------v---------+-----+
| PostgreSQL |               | MinIO Storage |   | Redis (Route Cache)   |
| (Database) |               | (Code Bucket) |   +-----------------------+
+------------+               +-------+-------+
                                     |
                                     v
                      +--------------+------------------+
                      | Kubernetes Pod Runner Pool      |
                      |  - Container 1: Node.js (Vite)   |
                      |  - Container 2: MinIO mc Syncer |
                      +---------------------------------+
```

---

## 🌟 Accomplished Features & System Capabilities

- [x] **Authentication & Security Context**: Stateless JWT Auth filter (`JwtAuthFilter`) with custom principal mapping & RBAC (`OWNER`, `EDITOR`, `VIEWER`).
- [x] **Multitenant Workspace Domain**: Complete project lifecycle, member management, soft deletes, and entity lifecycle auditing.
- [x] **Production Stripe Billing Engine**: Checkout API integration, Event-driven Webhook Router (`StripeEventRouter`) using the Strategy Pattern with built-in idempotency.
- [x] **Spring AI Integration & OpenRouter Provider**: Connected Spring AI with OpenRouter API for streaming LLM generation.
- [x] **FileTree Context Advisor (`FileTreeAdvisor`)**: Intercepts prompt execution to dynamically inject the project file tree structure into the system context.
- [x] **Precision Tool Calling (`read_files`)**: Uses Spring AI `@Tool` function calls so the LLM selectively reads source files before making code edits.
- [x] **Streaming XML Code Generation Protocol**: Streams real-time tokens via SSE structured in `<tool>`, `<message>`, and `<file>` XML blocks.
- [x] **Dynamic Kubernetes Sandbox & Runner Pool**: Pre-warmed `runner-pool` deployment in Kubernetes with dual-container pods (Node.js runtime + MinIO `mc` syncer).
- [x] **Spring Boot Reverse Proxy & Redis Gateway**: Built a reverse proxy service (`proxy-service`) resolving dynamic subdomains (`project-<id>.127.0.0.1.nip.io:8090`) to dynamic Pod IPs using Redis caching.
- [x] **Interactive Frontend Application**: React + Vite + TypeScript frontend showcasing live preview rendering in an iframe.

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Language & Runtime** | Java 25 (Virtual Threads Enabled) |
| **Backend Frameworks** | Spring Boot 4.x, Spring Web, Spring Security |
| **AI Integration** | Spring AI, OpenRouter API (GPT-5.6 / Llama / Claude) |
| **Database & Caching** | PostgreSQL 18, Redis 7, Hibernate 7 |
| **Object Storage** | MinIO Object Storage |
| **Payments** | Stripe Java SDK |
| **Container Orchestration**| Kubernetes (Kind / Minikube) |
| **Frontend** | React 18, Vite, TypeScript, Tailwind CSS 4, daisyUI 5 |

---

## 💻 Detailed Local Setup & Run Guide

### Prerequisites
Ensure the following tools are installed on your system:
- **Java 25 JDK**
- **Node.js (v20+) & npm**
- **Docker Desktop / Docker Engine**
- **Kind (Kubernetes in Docker)** or **Minikube**
- **kubectl CLI**

---

### Step 1: Clone the Repository
```bash
git clone https://github.com/Abhinay8381/buildrix-ai.git
cd buildrix-ai
```

---

### Step 2: Configure API Keys in `application.yaml`
Open `src/main/resources/application.yaml` and add your actual API keys:

```yaml
spring:
  ai:
    openai:
      api-key: YOUR_OPENROUTER_API_KEY_HERE   # Replace with your OpenRouter key
      base-url: https://openrouter.ai/api/v1
      chat:
        model: openai/gpt-5.6-luna            # Or choice of OpenRouter model

stripe:
  api:
    secret: YOUR_STRIPE_SECRET_KEY_HERE       # (Optional) Stripe Secret Key
  webhook:
    secret: YOUR_STRIPE_WEBHOOK_SECRET_HERE   # (Optional) Stripe Webhook Secret
```

---

### Step 3: Start Infrastructure Containers (PostgreSQL & MinIO)

Run the following commands to start PostgreSQL and MinIO:

```bash
# 1. Start PostgreSQL (Port 9010)
docker run -d --name buildrix-postgres \
  -e POSTGRES_DB=buildrix-DB \
  -e POSTGRES_USER=user \
  -e POSTGRES_PASSWORD=password \
  -p 9010:5432 postgres:18-alpine

# 2. Start MinIO (Ports 9000 & 9001)
docker run -d --name buildrix-minio \
  -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin123 \
  minio/minio server /data --console-address ":9001"
```

---

### Step 4: Build & Load the Proxy Service Image into Kind

1. **Build the Proxy Service Docker Image:**
   ```bash
   docker build -t buildrix-proxy:latest -f proxy-service/Dockerfile proxy-service
   ```

2. **Create the Kind Cluster:**
   ```bash
   kind create cluster --name buildrix
   ```

3. **Load the Docker Image into Kind Cluster:**
   ```bash
   kind load docker-image buildrix-proxy:latest --name buildrix
   ```

---

### Step 5: Deploy Kubernetes Resources

Apply all Kubernetes configurations:

```bash
# Create the Kubernetes namespace
kubectl create namespace buildrix-apps

# Apply all K8s manifests
kubectl apply -f k8s/redis.yml -n buildrix-apps
kubectl apply -f k8s/runner-pods.yml -n buildrix-apps
kubectl apply -f k8s/proxy-deployment.yml -n buildrix-apps
```

Verify pods are running:
```bash
kubectl get pods -n buildrix-apps
```

---

### Step 6: Port-Forward Kubernetes Services

Open two separate terminal windows to port-forward Redis and Reverse Proxy:

```bash
# Terminal 1: Port-Forward Redis to localhost:6379
kubectl port-forward svc/redis-service 6379:6379 -n buildrix-apps

# Terminal 2: Port-Forward Reverse Proxy to localhost:8090
kubectl port-forward svc/buildrix-proxy-svc 8090:80 -n buildrix-apps
```

---

### Step 7: Start Backend & Frontend

1. **Run Buildrix AI Main Backend (Port 8080):**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Run Frontend Application:**
   *(In a new terminal window)*
   ```bash
   cd buildrix-frontend
   npm install
   npm run dev
   ```

3. **Access App:**
   Open `http://localhost:8080` (or `http://localhost:5173`) in your browser to start generating, editing, and running React apps live! 🚀
