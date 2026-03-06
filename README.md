# Kafka-Based Financial Transaction System

An asynchronous, event-driven microservices ecosystem built with **Spring Boot**, **Kafka**, and **PostgreSQL** that demonstrates consistent financial processing.

### Running the app

1.  **Build JARs:**
```mvn clean package -DskipTests```
2.  **Run the containers in detached mode**
```docker-compose up --build -d```
```
[+] up 5/5                                                                                                                                                                  
 ✔ Image microservice-financial-kafka-transaction-producer Built                                                                                                       10.0s
 ✔ Container microservice-financial-kafka-zookeeper-1      Running                                                                                                     0.0s
 ✔ Container microservice-financial-kafka-postgres-1       Healthy                                                                                                     2.5s
 ✔ Container microservice-financial-kafka-kafka-1          Healthy                                                                                                     2.5s
 ✔ Container transaction-producer                          Recreated                                                                                                   1.9s
```

4. Open ``http://localhost:80`` in your browser

### Architecture Patterns

* **Transactional Outbox:** Prevents the Dual Write Problem by saving the transaction and an event to the same database atomically.
* **Event-Driven architecture:** Services communicate via Kafka topics rather than direct REST calls.
* **Idempotent Consumer:** The consumer tracks processed IDs to prevent duplicate financial settlements.
* **Dead Letter Topic (DLT):** Invalid messages (in this case negative amounts) are moved to a separate queue after 3 retries.


### Project Structure
* `/gateway`: NGINX API Gateway & Frontend.
* `/transaction-producer`: API, Transaction logic, and Outbox Relay.
* `/transaction-consumer`: Kafka Listener and Settlement logic.
* `docker-compose.yml`: Full microservice orchestration.
