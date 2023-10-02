package com.zaga.repo;


import com.zaga.entity.auth.UserCredentials;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthRepo implements PanacheMongoRepository<UserCredentials> {
  
}
