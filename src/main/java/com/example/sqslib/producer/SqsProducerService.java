package com.example.sqslib.producer;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SqsProducerService {

    private static final Logger log = LoggerFactory.getLogger(SqsProducerService.class);

    private final SqsTemplate sqsTemplate;

    public SqsProducerService(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void send(String queueName, Object payload) {
        sqsTemplate.send(sqsSendOptions -> sqsSendOptions.queue(queueName).payload(payload));

        log.info("Mensaje enviado a la cola: " + queueName);
    }
}