package com.zaga.entity.auth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties({ "id" })
@MongoEntity(collection = "ServiceListNew", database = "ObservabilityCredentials")
public class ServiceListNew  {

    /**
     * The name of the service.
     */
    private String serviceName;

    /**
     * The list of roles associated with the service.
     */
    private List<String> roles;

    private List<Rule> rules;
}
