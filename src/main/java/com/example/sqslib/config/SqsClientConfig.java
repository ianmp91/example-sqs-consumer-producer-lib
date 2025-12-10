package com.example.sqslib.config;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
public class SqsClientConfig {

    // Hacemos el endpoint dinámico, usando localhost:9324 como default (fuera de docker)
    // El microservicio B o C lo sobrescribirá a http://elasticmq:9324
    @Value("${spring.sqs.endpoint.uri:http://localhost:9324}")
    private String elasticMqEndpoint;

    // --- 1. SQS CLIENTE DE BAJO NIVEL (para ElasticMQ) ---
    @Primary
    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create(elasticMqEndpoint)) // Endpoint dinámico
                .credentialsProvider(AnonymousCredentialsProvider.create()) // Credenciales dummy
                .region(Region.US_EAST_1) // Región dummy
                .build();
    }

    // --- 2. SQS TEMPLATE (Para el Producer) ---
    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .build();
    }

    // --- 3. SQS LISTENER FACTORY (Para el Consumer) ---
    // Esto es NECESARIO para que @SqsListener funcione.
    @Bean
    public SqsMessageListenerContainerFactory<?> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {
        return SqsMessageListenerContainerFactory
                .builder()
                .sqsAsyncClient(sqsAsyncClient)
                // Opcional: Configuración específica para el manejo de mensajes (ej. número de hilos, etc.)
                // .maxNumberOfMessages(1)
                .build();
    }
}
