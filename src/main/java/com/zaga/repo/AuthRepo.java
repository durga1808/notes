package com.zaga.repo;


import com.zaga.entity.auth.UserCredentials;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthRepo implements PanacheMongoRepository<UserCredentials> {
  

    public UserCredentials getUser(String username) {
        PanacheQuery<UserCredentials> data = UserCredentials.find("username=?1", username);
        UserCredentials userData = data.firstResult();
        return userData;

    }
}
