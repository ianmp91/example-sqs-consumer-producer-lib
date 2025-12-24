# SQS Consumer Producer Library (A)

Shared library designed to standardize message production and consumption in AWS SQS using **Java 25** and **Spring Boot 4**. It includes base configuration to connect with ElasticMQ (local environment) and real AWS SQS.

## 🛠 Tech Stack

* **Java:** 25
* **Framework:** Spring Boot 4.x
* **Build Tool:** Gradle (Groovy DSL)
* **Cloud:** Spring Cloud AWS 4.0.0-M1 (`spring-cloud-aws-starter-sqs`)

## AWS SNS and SQS Explanation

Amazon SNS and Amazon SQS are two of the most important messaging services from AWS.
Both serve to decouple applications (making parts of your system work independently), but they do so in very different ways.

| Feature | Amazon SNS (Notifications) | Amazon SQS (Queues) |
| --- | --- | --- |
| Type | Pub/Sub (One to many) | Queue (One to one*) |
| Delivery | Push (Immediate) | Pull (On demand/Polling) |
| Persistence | No (Ephemeral) | Yes (Durable, configurable) |
| Use cases | Alerts, push notifications, fan-out. | Decoupling, batch processing, load buffering. |
| Consumers | Multiple subscribers receive a copy. | One consumer processes and deletes. |

Summary to remember:

* Use **SNS** if you want many systems or people to know about something immediately.
* Use **SQS** if you want a system to process tasks at its own pace without losing information.

## 📦 Installation for Microservices

Add the dependency to your `build.gradle` file in the microservices (assuming this library is published in your local repository).

```groovy
repositories {
    mavenLocal() // Add to the microservice
    mavenCentral()
}


dependencies {
    implementation 'com.example.sqslib:sqs-consumer-producer-lib:0.0.1-SNAPSHOT'
}
```

## ⚙️ Configuration

This library auto-configures the SQS client. You must provide the following properties in the `application.yml` of the implementing microservice:

```yaml
spring:
  application:
    name: sqs-consumer-producer-lib
  main:
    allow-bean-definition-overriding: true
  cloud:
    aws:
      region:
        static: us-east-1 # An arbitrary region
      # Specific configuration for SQS pointing to ElasticMQ
      sqs:
        # Points to the ElasticMQ container port
        endpoint: ${SPRING_CLOUD_AWS_SQS_ENDPOINT:http://localhost:9324}
      credentials:
        # Dummy credentials that satisfy the AWS SDK requirement
        access-key: dummy
        secret-key: dummy
```

## 🚀 Usage

**Producer**

The library exposes an `SqsProducerService` Bean that you can inject to send messages.

**Consumer**

**Note:** This library does NOT implement `@SqsListener`. Each microservice must define its own listeners according to its business logic.

## 🧪 Local Testing (ElasticMQ)

Ensure you have ElasticMQ running to simulate SQS. It is expected that the `custom.conf` file of ElasticMQ defines the necessary queues.

`elasticmq/custom.conf`

```bash
# This is the configuration file for ElasticMQ
node-address {
  host = "*"
  port = 9324
  protocol = http
}

rest-sqs {
  enabled = true
  bind-port = 9324
  bind-hostname = "0.0.0.0"
}

queues {
  # Queue for (B) to send and (C) to receive/consume
  "cola-aws-sqs-1" {
    defaultVisibilityTimeout = 5 seconds
    delay = 0 seconds
    receiveMessageWait = 0 seconds
  }

  # Queue for (C) to send the response and (B) to receive/consume
  "cola-aws-sqs-2" {
    defaultVisibilityTimeout = 5 seconds
    delay = 0 seconds
    receiveMessageWait = 0 seconds
  }
}
```

```bash
docker-compose up -d
```

## 🚀 Execution

```bash
make clean
make build
make publish // Publishes to your local repo
```