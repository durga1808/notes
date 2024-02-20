package com.zaga.controller;

import com.zaga.entity.cluster_utilization.OtelCluster_utilization;
import com.zaga.handler.Cluster_utilizationHandler;
import com.zaga.repo.Cluster_utilizationRepo;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/cluster_utilization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Cluster_utilizationController {
    
    @Inject
    Cluster_utilizationHandler cluster_utilizationHandler;

    @Inject
    Cluster_utilizationRepo cluster_utilizationRepo;

    @POST
    @Path("/cluster_utilization")
    public OtelCluster_utilization createCluster_utilization(OtelCluster_utilization otelCluster_utilization){
       OtelCluster_utilization utilization = new OtelCluster_utilization();
       return utilization;
    }
    

}
