package com.zaga.handler;

import com.zaga.entity.otellog.OtelLog;
import com.zaga.repo.LogCommandRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LogCommandHandler {
    @Inject
    LogCommandRepo logCommandRepo;

    public void createLogProduct(OtelLog logs) {
        logCommandRepo.persist(logs);
    }

}
