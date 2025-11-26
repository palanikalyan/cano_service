package com.dfpt.canonical.service;

import com.dfpt.canonical.model.CanonicalTrade;
import com.dfpt.canonical.model.OutboxEvent;
import com.dfpt.canonical.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Creates outbox events for event sourcing pattern
 */
@Service
public class OutboxService {

    @Autowired
    private OutboxRepository repo;

    /**
     * Create an outbox event for a trade
     */
    public void create(CanonicalTrade trade) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setAggregateId(trade.getId());
        event.setEventType("CANONICAL.CREATED");
        event.setPayload(mapper.writeValueAsString(trade));
        event.setStatus("PENDING");
        event.setCreatedAt(LocalDateTime.now());

        repo.save(event);
    }
}
