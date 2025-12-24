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

/**
 * @author ian.paris
 * @since 2025-12-15
 */
@Configuration
public class SqsClientConfig {

    @Value("${spring.cloud.aws.sqs.endpoint:http://localhost:9324}")
    private String elasticMqEndpoint;

    // --- 1. SQS LOW-LEVEL CLIENT (by ElasticMQ) ---
    @Primary
    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create(elasticMqEndpoint)) // Endpoint dinámico
                .credentialsProvider(AnonymousCredentialsProvider.create()) // Credenciales dummy
                .region(Region.US_EAST_1) // Región dummy
                .build();
    }

    // --- 2. SQS TEMPLATE (by Producer) ---
    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .build();
    }

    // --- 3. SQS LISTENER FACTORY (by Consumer) ---
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
