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

### Service Endpoints

* **RabbitMQ Management UI**: http://localhost:15672  
  * Login: `admin`
  * Password: `password`

* **MinIO Console**: http://localhost:9001  
  * Login: `admin`
  * Password: `password`

* **Qdrant API**: http://localhost:6333  
  * Accessing this endpoint should return the system status in JSON format.


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
