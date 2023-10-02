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
@MongoEntity(collection = "UserCreds", database = "ObservabilityCredentials")
public class UserCredentials {
    private String username;
    private String password;
    private List<String> roles;
}
