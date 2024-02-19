package com.zaga.handler;

import com.zaga.entity.otelevent.OtelEvents;
import com.zaga.repo.EventRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class EventCommandHandler {
    
    @Inject
    EventRepo repo ;
    public OtelEvents createEvents (OtelEvents events) {
        repo.persist(events);
        return events;
    }
}
