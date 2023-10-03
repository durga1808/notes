package com.zaga.entity.auth;

import java.util.List;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
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
public class UserCredentials extends PanacheMongoEntity {
    public ObjectId id;
    private String username;
    private String password;
    private List<String> roles;
}
