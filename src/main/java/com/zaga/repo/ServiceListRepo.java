package com.zaga.repo;

import com.zaga.entity.auth.ServiceList;
import com.zaga.entity.auth.ServiceListNew;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServiceListRepo implements PanacheMongoRepository<ServiceListNew> {

    public ServiceListNew findMetricByServiceName(String serviceName) {
        return find("serviceName = ?1 and rules.ruleType = ?2", serviceName, "metric").firstResult();
    }

    public ServiceListNew findByServiceNameAndRuleType(String serviceName, String ruleType) {
        return find("serviceName = ?1 and rules.ruleType = ?2", serviceName, ruleType).firstResult();
    }
}
