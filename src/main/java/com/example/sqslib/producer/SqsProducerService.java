package com.example.sqslib.producer;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author ian.paris
 * @since 2025-12-15
 */
@Slf4j
@Service
public class SqsProducerService {

    private final SqsTemplate sqsTemplate;

    public SqsProducerService(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void send(String queueName, Object payload) {
        sqsTemplate.send(sqsSendOptions -> sqsSendOptions.queue(queueName).payload(payload));
        log.info("Message sent to the queue: " + queueName);
    }
}
