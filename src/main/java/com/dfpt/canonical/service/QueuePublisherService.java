package com.dfpt.canonical.service;

import com.dfpt.canonical.config.ActiveMQConfig;
import com.dfpt.canonical.model.CanonicalTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes trade messages to ActiveMQ
 */
@Service
public class QueuePublisherService {

    private static final Logger logger = LoggerFactory.getLogger(QueuePublisherService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Publish a trade to the ActiveMQ queue
     */
    public void publishToQueue(CanonicalTrade trade) {
        try {
            jmsTemplate.convertAndSend(ActiveMQConfig.CANONICAL_QUEUE, trade);
            logger.info("Published to queue: {}", trade.getOrderId());
        } catch (Exception e) {
            logger.error("Queue publish failed: {}", e.getMessage(), e);
            throw new RuntimeException("Queue publishing failed", e);
        }
    }
}
