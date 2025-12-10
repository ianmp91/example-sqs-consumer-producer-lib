# SQS Consumer Producer Library (A)

Librería compartida diseñada para estandarizar la producción y consumo de mensajes en AWS SQS utilizando **Java 25** y **Spring Boot 4**. Incluye configuración base para conectar con ElasticMQ (entorno local) y AWS SQS real.

## 🛠 Tech Stack

* **Java:** 25
* **Framework:** Spring Boot 4.x
* **Build Tool:** Gradle (Groovy DSL)
* **Cloud:** Spring Cloud AWS 4.0.0-M1 (`spring-cloud-aws-starter-sqs`)

## 📦 Instalación para Microservicios

Añade la dependencia a tu archivo `build.gradle` (asumiendo que esta librería está publicada en tu repositorio local o Nexus):

```groovy
repositories {
    mavenLocal() //Agregar al miroservicio
    mavenCentral()
}


dependencies {
    implementation 'com.example.sqslib:sqs-consumer-producer-lib:0.0.1-SNAPSHOT'
}
```

## ⚙️ Configuración

Esta librería autoconfigura el cliente SQS. Debes proveer las siguientes propiedades en el application.yml del microservicio que la implemente:
```yaml
spring:
  application:
    name: sqs-consumer-producer-lib
  main:
    allow-bean-definition-overriding: true
  cloud:
    aws:
      region:
        static: us-east-1 # Una región arbitraria
      # Configuración específica para SQS que apunta a ElasticMQ
      sqs:
        endpoint:
          # Apunta al puerto del contenedor de ElasticMQ
          uri: http://localhost:9324
      credentials:
        # Credenciales dummy que cumplen con el requisito del SDK de AWS
        access-key: dummy
        secret-key: dummy
```
## 🚀 Uso

Producer

La librería expone un Bean SqsProducerService que puedes inyectar para enviar mensajes.

Consumer

Nota: Esta librería NO implementa los @SqsListener. Cada microservicio debe definir sus propios listeners según su lógica de negocio.

## 🧪 Testing Local (ElasticMQ)

Asegúrate de tener levantado ElasticMQ para simular SQS. Se espera que el archivo custom.conf de ElasticMQ defina las colas necesarias.

```bash
docker-compose up -d
```

## 🚀 Ejecución

```bash
make clean
make build
make publish //Publica en tu local
```
