# VisionService

An image processing and analysis microservice built with Spring Boot, a containerized infrastructure stack, and local/cloud-based Artificial Intelligence models (Spring AI).

---

## Architecture & Tech Stack

The application integrates with a complete runtime environment hosted in Docker:

* **Java 21** (LTS) & **Spring Boot 3.4.2**
* **MongoDB 6.0** – Stores metadata and image analysis results.
* **RabbitMQ 3.12** – Handles asynchronous communication and task queueing.
* **MinIO** – AWS S3-compatible local server for storing binary files (images).
* **Qdrant** – Vector database for visual similarity search (embeddings).
* **Ollama (Llava)** – Local Vision LLM used for analyzing image content.
* **OpenAI (Spring AI)** – Used as the default provider for vector embeddings.

---

##  Prerequisites

Before running the application, ensure you have the following installed:
* Docker & Docker Compose
* JDK 21
* Maven 3.x or higher (or use the provided `mvnw`)

---

## Step-by-Step Setup

### 1. Start the Infrastructure (Docker)
In the root directory of the project, where the `docker-compose.yml` file is located, run the following command:

```bash
docker compose up -d

### 2. Monitor Ollama Model Initialization

During the first startup, Ollama needs to download and initialize the AI model, which may take a few minutes. It is recommended to monitor the logs to confirm the model has been downloaded and started correctly:

```bash
docker logs -f visionservice-ollama
```

Wait until the logs confirm the model is ready before sending any requests to the application.

---

### Service Endpoints

* **RabbitMQ Management UI**: http://localhost:15672
  * Login: `admin`
  * Password: `password`

* **MinIO Console**: http://localhost:9001
  * Login: `admin`
  * Password: `password`

* **Qdrant API**: http://localhost:6333
  * Accessing this endpoint should return the system status in JSON format.

* **Grafana**: http://localhost:3000
  * Login: `admin`
  * Password: `admin`
  * Used for visualizing application metrics (Prometheus), logs (Loki), and traces (Tempo) in dashboards, all pre-provisioned as data sources.

* **Loki**: http://localhost:3100
  * Log aggregation backend. Container logs are shipped by **Promtail**, which tails Docker container stdout via the Docker socket for every service labeled `logging=promtail`.

* **Tempo**: http://localhost:3200 (OTLP gRPC: 4317, OTLP HTTP: 4318)
  * Distributed tracing backend. The application exports traces via OTLP/HTTP directly to Tempo. Traces are explored from within **Grafana** (Explore → Tempo), including trace-to-logs and service graph correlation.

---

## Observability – Grafana Dashboard Setup

The application exposes metrics that are scraped by Prometheus and visualized in Grafana. After starting the infrastructure with `docker compose up -d`, you can import a ready-made dashboard for monitoring Spring Boot 3.x applications:

1. Open Grafana at http://localhost:3000 and log in using the credentials above.
2. In the left-hand menu, go to **Dashboards**.
3. Click the **New** button (top right) and select **Import**.
4. In the **Import via grafana.com** field, enter the dashboard ID `19004` (Spring Boot 3.x Statistics) and click **Load**.
5. On the next screen, select the **Prometheus** data source from the dropdown list.
6. Click **Import** to finish.

The dashboard will now display JVM, HTTP, and Spring Boot Actuator metrics for the application.

### Additional Dashboard – Spring Boot Observability (ID `17175`)

A second dashboard, **Spring Boot Observability** (`17175`), can also be imported the same way (**Dashboards → New → Import**, enter `17175`, click **Load**).

When prompted, assign the **Prometheus** data source for metric panels and the **Loki** data source for log panels — both are already provisioned, so the log panels will display real application logs collected via Promtail.

---

## Logs & Traces (Loki + Tempo)

Application logs include `traceId`/`spanId` in every line (via Micrometer Tracing + the Logback pattern in `logback-spring.xml`), and are pushed straight to Loki even when the app runs locally from the IDE — no need to containerize it.

### Viewing application logs in Grafana

1. Open Grafana at http://localhost:3000 and go to **Explore** (left sidebar).
2. At the top left, click the data source dropdown and select **Loki**.
3. Make sure the query editor is in **Code** mode (top right of the query row — switch from "Builder" to "Code" if needed).
4. In the query field, type:
   ```
   {app="VisionService"}
   ```
5. Click **Run query** (top right).

You should see a **Logs volume** histogram and the log lines below it, including lines with `traceId=...` and `spanId=...`.

For infrastructure logs, use `{service="rabbitmq"}`, `{service="mongodb"}`, etc. (any label shown under "Common labels" / "Fields" in the Loki explorer).

### Jumping from a log line to its trace in Tempo

1. In the Loki log list, click the small arrow (`>`) on the left of a log line to expand it.
2. Scroll to the **Links** section.
3. Next to **TraceID**, click the **Tempo** button — this opens the matching trace directly in Tempo's trace view.

### Exploring traces directly in Tempo

1. In Grafana, go to **Explore** and select the **Tempo** data source.
2. Use the **TraceQL** tab and paste a trace ID (e.g. copied from a log line), or use **Search** to browse recent traces by service/operation.
3. The **Service Graph** tab shows a node graph of how services call each other (requires the `metrics_generator` writing to Prometheus, already configured).

### Containerizing the app

If you run the app itself as a container in this compose, override `loki.url` to `http://loki:3100/loki/api/v1/push` (e.g. via `SPRING_APPLICATION_JSON` or an env var mapped to `loki.url`).

---

## Testing Photo Upload (Postman)

You can test the photo upload endpoint using the following cURL request (can be imported into Postman):

```bash
curl --location 'http://localhost:8080/api/photos/upload' \
--form 'file=@"/path/to/your/photo.jpg"'
```

### Postman Configuration

After importing the request into Postman:

1. Open the **Body** tab.
2. Select **form-data**.
3. In the `file` row, manually choose an image file from your computer.
4. Send the request.

The endpoint expects a multipart file under the `file` field.

---

## Testing Photo Search (Postman)

You can search the photo database using a natural language query via the following endpoint:

```
http://localhost:8080/api/photos/search?query=human face or person
```

In the `query` parameter, enter any description of what you want to find in the photo database — for example, a scene, object, or person. The application will perform a semantic similarity search and return matching results.
