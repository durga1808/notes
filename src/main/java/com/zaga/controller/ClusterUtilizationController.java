package com.zaga.controller;

import com.zaga.entity.clusterutilization.OtelClusterUutilization;
import com.zaga.handler.ClusterUtilizationHandler;
import com.zaga.repo.ClusterUtilizationRepo;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/cluster_utilization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClusterUtilizationController {
    
    @Inject
    ClusterUtilizationHandler cluster_utilizationHandler;

    @Inject
    ClusterUtilizationRepo cluster_utilizationRepo;

    @POST
    @Path("/create_clusterUtilization")
    public Response createEvent (OtelClusterUutilization cluster_utilization){
      try {
        cluster_utilizationHandler.createCluster_utilization(cluster_utilization);
        return Response.status(200).entity(cluster_utilization).build();

      } catch (Exception e) {
        return Response.status(500).entity(e.getMessage()).build();
        
      }
    

}
}
