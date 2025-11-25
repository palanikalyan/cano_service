package com.dfpt.canonical.service;

import com.dfpt.canonical.config.ActiveMQConfig;
import com.dfpt.canonical.model.CanonicalTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueuePublisherService {

    private static final Logger logger = LoggerFactory.getLogger(QueuePublisherService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public void publishToQueue(CanonicalTrade trade) {
        try {
            jmsTemplate.convertAndSend(ActiveMQConfig.CANONICAL_QUEUE, trade);
            logger.info("Published trade to ActiveMQ queue: {}", trade.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to publish trade to ActiveMQ queue: {}", e.getMessage(), e);
            throw new RuntimeException("Queue publishing failed", e);
        }
    }
}
