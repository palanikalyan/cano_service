package com.dfpt.canonical.listener;

import com.dfpt.canonical.config.ActiveMQConfig;
import com.dfpt.canonical.model.CanonicalTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

// @Component  // Commented out to disable auto-consuming - uncomment to enable listener
public class QueueListenerService {

    private static final Logger logger = LoggerFactory.getLogger(QueueListenerService.class);

    @JmsListener(destination = ActiveMQConfig.CANONICAL_QUEUE)
    public void receiveMessage(CanonicalTrade trade) {
        logger.info("Received message from ActiveMQ queue: Order ID = {}, Fund Code = {}, Amount = {}", 
                    trade.getOrderId(), 
                    trade.getFundCode(), 
                    trade.getAmount());
    
    }
}
