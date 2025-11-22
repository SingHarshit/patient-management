# 🏥 Patient Management Service

A microservice responsible for managing patient records and publishing **event-driven notifications** to Kafka using **Protobuf**. This service is part of a larger healthcare/microservices ecosystem, and acts as the **source of truth** for patient data.

---

## 🚀 Features

### 👤 Patient Management

* Create new patients
* Store patient information in MySQL
* Validate patient input
* Provide REST API endpoints for CRUD operations

### 📡 Event-Driven Architecture

* Publishes **Protobuf-based PatientEvent messages** on every patient creation
* Sends events to Kafka topic: **`patient-event-topic`**
* Event includes:

  * `patientId`
  * `name`
  * `email`
  * `eventType = PATIENT_CREATED`

### 🧱 Tech Used

* Java 21
* Spring Boot
* MySQL
* Kafka (KRaft mode)
* Docker
* Protobuf (Google Protocol Buffers)
* REST APIs

---

## 🛠 Architecture Overview

### 🧩 Microservices Context

The Patient Management Service is designed as part of a modular **microservices architecture**, where each service owns its data and domain logic.

Key characteristics:

* Each service runs independently
* Loose coupling via **events** and **gRPC APIs**
* Independent deployment lifecycle
* Services communicate through Kafka (async) or gRPC (sync)

### 🔗 gRPC in the Ecosystem

Although the Patient Service primarily publishes Kafka events, other services in the system may interact with it through **gRPC for low‑latency, typed, and efficient communication**.

Possible integrations:

* Analytics Service calling Patient Service via gRPC to fetch enriched patient info
* Reporting Service aggregating data via gRPC
* Internal system checks for patient verification

gRPC Benefits:

* High‑performance binary protocol
* Protobuf-based schema sharing
* Strongly typed contract
* Better performance than REST for internal microservice calls

```
 Patient Service  ──────►  Kafka (topic: patient-event-topic)
       │                         ▲
       │ REST API                │
       ▼                         │
   MySQL DB                Analytics Service
```

* Patient is created → event is generated
* Event is serialized using **Protobuf → byte[]**
* Sent to Kafka via `KafkaTemplate<String, byte[]>`
* Other services (analytics, billing, etc.) consume the event

---

## 📁 Project Structure

```
patient-service/
├── src/main/java/com/pm/patientservice
│   ├── controller/
│   ├── model/
│   │   └── Patient.java
│   ├── kafka/
│   │   └── kafkaProducer.java
│   ├── service/
│   └── repository/
│
├── src/main/proto/
│   └── patient_event.proto
│
├── src/main/resources/
│   ├── application.yml
│
├── Dockerfile
├── README.md
```

---

## 🔥 Kafka Event (Protobuf)

Protobuf schema (`patient_event.proto`):

```proto
message PatientEvent {
  string patientId = 1;
  string name = 2;
  string email = 3;
  string eventType = 4;
}
```

Producer code sends:

```java
kafkaTemplate.send("patient-event-topic", event.toByteArray());
```

---

## ⚙️ Environment Variables

Example for running inside Docker or on server:

```
SPRING_DATASOURCE_URL=jdbc:mysql://patient-service-db:3306/db
SPRING_DATASOURCE_USERNAME=admin_user
SPRING_DATASOURCE_PASSWORD=password

SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:19092
SPRING_KAFKA_PRODUCER_BOOTSTRAP_SERVERS=kafka:19092
```

---

## 🐋 Running with Docker

### Build Image

```bash
docker build -t patient-service .
```

### Run Container

```bash
docker run -p 4000:4000 patient-service
```

### With docker-compose (recommended)

```yaml
patient-service:
  build: .
  ports:
    - "4000:4000"
  depends_on:
    - kafka
    - patient-service-db
```

---

## 🧪 API Endpoints

### ▶ Create Patient

**POST** `/api/patients`

Example JSON:

```json
{
  "name": "John Doe",
  "email": "johndoe@example.com"
}
```

On success:

* Patient stored in MySQL
* Protobuf event pushed to Kafka

---

## 🧩 How to Test Kafka Output

Inside Kafka container:

1️⃣ List topics:

```bash
kafka-topics.sh --bootstrap-server kafka:19092 --list
```

2️⃣ Consume messages:

```bash
kafka-console-consumer.sh \
  --bootstrap-server kafka:19092 \
  --topic patient-event-topic \
  --from-beginning
```

You will see Protobuf bytes.

---

## 📊 What Happens Next

This service plugs into your Analytics Service:

* Analytics-Service consumes byte[]
* Uses `PatientEvent.parseFrom(data)` to decode
* Processes patient activity

---

## 📌 Future Enhancements

* Update/Delete patient events
* Transactional outbox pattern
* Schema registry integration
* gRPC interface for inter-service communication

---

## 📝 License

MIT License
