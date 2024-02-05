package com.zaga.controller;



import com.zaga.entity.pod.OtelPodMetric;
import com.zaga.repo.PodCommandRepo;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/podMetrics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PodController {

@Inject
PodCommandRepo podCommandRepo;

@POST
@Path("/create")
public Response createPodMetric(OtelPodMetric podMetric) {
    podCommandRepo.persist(podMetric);
    return Response.status(Response.Status.CREATED).entity(podMetric).build();
}
}
