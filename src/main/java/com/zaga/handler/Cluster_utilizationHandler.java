package com.zaga.handler;

import com.zaga.repo.Cluster_utilizationRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Cluster_utilizationHandler {

    @Inject
    Cluster_utilizationRepo cluster_utilizationRepo;
    
}
