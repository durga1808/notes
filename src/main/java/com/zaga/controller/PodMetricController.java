package com.zaga.controller;

import com.zaga.entity.podMetric.PodMetric;
import com.zaga.handler.PodMetricsHandler;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/PodMetrics")
public class PodMetricController {

    @Inject 
    PodMetricsHandler podMetricsHandler;
    
    @POST
    @Path("/create")
    public Response createProduct(PodMetric metric) {
        try {
            //System.out.println("----------------");
            podMetricsHandler.createPodMetrics(metric);
            return Response.status(200).entity(metric).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }  


}
