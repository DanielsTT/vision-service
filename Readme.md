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
  * Used for visualizing application metrics (collected via Prometheus) in the form of dashboards.

* **Jaeger UI**: http://localhost:16686
  * Used for distributed tracing – allows you to inspect individual requests as they flow through the application and its dependencies.

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

 **Note**: This dashboard also expects a **Loki** data source (used for log panels). Since Loki is not part of this stack, you need to add a fake/placeholder Loki data source so the import doesn't fail:

1. Go to **Connections → Data sources → Add data source**.
2. Select **Loki**.
3. Enter any URL (e.g. `http://localhost:3100`) – it doesn't need to actually work.
4. Click **Save & test** (the connection test may fail, that's fine) and save the data source.
5. Now import dashboard `17175` and, when prompted, assign the **Prometheus** data source for metric panels and the fake **Loki** data source for log panels.

The log panels will remain empty, but all metric-based panels will display correctly.

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
