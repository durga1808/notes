package com.zaga.controller;

import com.zaga.entity.auth.UserCredentials;
import com.zaga.entity.auth.Rule;
import com.zaga.entity.auth.ServiceListNew;
import com.zaga.handler.AuthCommandHandler;
import com.zaga.repo.AuthRepo;
import com.zaga.repo.ServiceListRepo;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

@Path("/AuthController")
public class AuthController {

    @Inject
    AuthCommandHandler authCommandHandler;

    @Inject
    AuthRepo repo;


    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateWithDB(final UserCredentials credentials) {
        try {
            Response response = authCommandHandler.getUserInfo(credentials);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/register")
    public Response saveUserData(final UserCredentials credentials) {
        try {
            Response response = authCommandHandler.saveUserInfo(credentials);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    /**
     * Add a new service to the user's service list.
     *
     * @param serviceList Service details to be added.
     * @return Response indicating success or failure of adding the service.
     */
    // @POST
    // @Path("/addServiceList")
    // public Response addServiceList(final ServiceList serviceList) {
    //     try {
    //         Response response = authCommandHandler.addServiceList(serviceList);
    //         return response;
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return Response.serverError().build();
    //     }
    // }

    @Inject
    ServiceListRepo serviceListRepo;

    /**
     * Add a new service to the user's service list.
     *
     * @param serviceList Service details to be added.
     * @return Response indicating success or failure of adding the service.
     */
    @POST
    @Path("/addServiceListNew")
    public void addServiceListNew(final ServiceListNew serviceListNew) {
        try {
            serviceListRepo.persist(serviceListNew);
            System.out.println("Output from api " + serviceListNew);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @PUT
    @Path("/updateServiceList")
    public void updateServiceList(ServiceListNew serviceListNew) {
        try {
            // Validate that serviceName and ruleType are provided
            String serviceName = serviceListNew.getServiceName();
            String ruleType = serviceListNew.getRules().get(0).getRuleType();

            if (serviceName == null || ruleType == null) {
                // Handle validation failure, e.g., return a 400 Bad Request response
                throw new WebApplicationException("serviceName and ruleType must be provided", Response.Status.BAD_REQUEST);
            }

            // Check if the service name exists in the database
            ServiceListNew existingService = serviceListRepo.findByServiceNameAndRuleType(serviceName, ruleType);

            if (existingService != null) {
                // Update the existing entry based on the rule type
                updateExistingService(existingService, serviceListNew);
                // Use the update method to persist changes
                serviceListRepo.update(existingService);
                System.out.println("Updated service: " + existingService.getServiceName());
            } else {
                // Service name doesn't exist, throw a 404 Not Found response or handle accordingly
                throw new WebApplicationException("Service not found", Response.Status.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception or log it appropriately
            // Return an HTTP response indicating failure if needed
            // Example: throw new WebApplicationException("Failed to update service", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
     private void updateExistingService(ServiceListNew existingService, ServiceListNew newService) {
        String ruleType = newService.getRules().get(0).getRuleType();
        
        if ("metric".equals(ruleType)) {
            Rule existingRule = existingService.getRules().get(0);
            Rule newRule = newService.getRules().get(0);
            existingRule.setMemoryConstraint(newRule.getMemoryConstraint());
            existingRule.setMemoryLimit(newRule.getMemoryLimit());
            existingRule.setCpuConstraint(newRule.getCpuConstraint());
            existingRule.setCpuLimit(newRule.getCpuLimit());
            existingRule.setStartDateTime(newRule.getStartDateTime());
            existingRule.setExpiryDateTime(newRule.getExpiryDateTime());
            System.out.println("Updated metric rule for service: " + existingService.getServiceName());
        } else if ("trace".equals(ruleType)) {
            Rule existingRule = existingService.getRules().get(0);
            Rule newRule = newService.getRules().get(0);
            existingRule.setDuration(newRule.getDuration());
            existingRule.setDurationConstraint(newRule.getDurationConstraint());
            existingRule.setStartDateTime(newRule.getStartDateTime());
            existingRule.setExpiryDateTime(newRule.getExpiryDateTime());
            System.out.println("Updated trace rule for service: " + existingService.getServiceName());
        } else if ("log".equals(ruleType)) {
            Rule existingRule = existingService.getRules().get(0);
            Rule newRule = newService.getRules().get(0);
            existingRule.setSeverityConstraint(newRule.getSeverityConstraint());
            existingRule.setSeverityText(newRule.getSeverityText());
            existingRule.setStartDateTime(newRule.getStartDateTime());
            existingRule.setExpiryDateTime(newRule.getExpiryDateTime());
            System.out.println("Updated log rule for service: " + existingService.getServiceName());
        } else {
            System.out.println("Not matched rule type: " + ruleType);
        }
    }



  
    @POST
    @Path("/getServiceList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceList(final UserCredentials userCredentials) {
        try {
            Response response = authCommandHandler.getServiceList(userCredentials);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }




    // @POST
    // @Path("/forgotPassword")
    // public Response forgotPassword(final UserCredentials credentials) {
    // try {
    // UserCredentials existingUser =
    // authCommandHandler.getUserInfoByUsername(credentials.getUsername());
    // if (existingUser == null) {
    // return Response.status(Response.Status.NOT_FOUND).entity("User not
    // found").build();
    // }
    // authCommandHandler.updateUserPassword(existingUser,
    // credentials.getPassword());
    @POST
    @Path("/forgotPassword")
    public Response forgotPassword(UserCredentials credentials) {
        try {

            authCommandHandler.updateUserCredentials(credentials);

            return Response.ok("Password updated successfully").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}
