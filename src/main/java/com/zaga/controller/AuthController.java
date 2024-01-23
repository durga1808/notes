package com.zaga.controller;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.zaga.entity.auth.Rule;
import com.zaga.entity.auth.ServiceListNew;
import com.zaga.entity.auth.UserCredentials;
import com.zaga.handler.AuthCommandHandler;
import com.zaga.repo.AuthRepo;
import com.zaga.repo.ServiceListRepo;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/AuthController")
public class AuthController {

  @Inject
  AuthCommandHandler authCommandHandler;

  @Inject
  AuthRepo repo;

  @Inject
  ServiceListRepo serviceListRepo;

  @POST
  @Path("/login")
  @Produces(MediaType.APPLICATION_JSON)
  public Response authenticateWithDB(final UserCredentials credentials) {
      try {
          Response response = authCommandHandler.getUserInfo(credentials);
       
          if (response.getStatus() == Response.Status.OK.getStatusCode()) {
              return response;
          } else {
              String errorMessage = "Incorrect credentials";
              return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessage).build();
          }
      } catch (Exception e) {
          e.printStackTrace();
          String errorMessage = "Authentication failed due to an internal error";
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage).build();
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

    // @POST
    // @Path("/getClusterInfo")
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response getClusterInfo(final UserCredentials credentials){
    //        try {
    //         Response response = authCommandHandler.getClusterInfo(credentials);
    //         return response;
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return Response.serverError().build();
    //     }
    // }

    // @POST
    // @Path("/register")
    // public Response saveUserData(final UserCredentials credentials) {
    //     try {
    //         Response response = authCommandHandler.saveUserInfo(credentials);
    //         return response;
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return Response.serverError().build();
    //     }
    // }


    @PUT
    @Path("/clusterDataUpdate")
    public Response updateUserData(final UserCredentials credentials){
       try {
            Response response = authCommandHandler.updateUserInfo(credentials);
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


  @POST
  @Path("/addServiceListNew")
  public Response addServiceListNew(final ServiceListNew serviceListNew) {
      try {
  
          List<Rule> rules = serviceListNew.getRules();
          String serviceName = serviceListNew.getServiceName();
  
          if (rules != null && !rules.isEmpty()) {
              String ruleType = rules.get(0).getRuleType();
  
              // Check if a service with the same serviceName and ruleType exists
              if (isServiceAlreadyExists(serviceName, ruleType, rules)) {
                  return Response.status(Response.Status.BAD_REQUEST)
                                 .entity("Service with serviceName '" + serviceName + "' and ruleType '" + ruleType + "' already exists.")
                                 .build();
              }
  
              // Check if a service with the same serviceName exists
              ServiceListNew existingService = serviceListRepo.findByServiceName(serviceName);
  
              if (existingService != null) {
                  // Check if the existing service already has "log", "trace", and "metrics" rule types
                  if (hasLogTraceMetricsRuleTypes(existingService)) {
                      return Response.status(Response.Status.BAD_REQUEST)
                                     .entity("Cannot persist rules. The existing service already has 'log', 'trace', and 'metrics' rule types.")
                                     .build();
                  }
  
                  List<Rule> existingRules = existingService.getRules();
                  existingRules.addAll(rules);
  
                  existingService.setRules(existingRules);
  
                  serviceListRepo.update(existingService);
  
                  return Response.status(Response.Status.OK).build();
              }
          }
  
          // If no existing service, persist the new service
          serviceListRepo.persist(serviceListNew);
  
          return Response.status(Response.Status.OK).build();
  
      } catch (Exception e) {
          System.out.println("Error processing request: " + e);

          return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("Internal Server Error: " + e.getMessage())
                         .build();
      }
  }
  

// Additional method to check if the existing service already has "log", "trace", and "metrics" rule types
private boolean hasLogTraceMetricsRuleTypes(ServiceListNew existingService) {
  
    List<Rule> existingRules = existingService.getRules();

    // Collect unique rule types from existing rules
    Set<String> uniqueRuleTypes = existingRules.stream()
                                               .map(Rule::getRuleType)
                                               .collect(Collectors.toSet());

    // Check if "log", "trace", and "metrics" rule types are present
    return uniqueRuleTypes.contains("log") && uniqueRuleTypes.contains("trace") && uniqueRuleTypes.contains("metrics");
}


  
  private boolean isServiceAlreadyExists(String serviceName, String ruleType, List<Rule> rules) {
      return serviceListRepo.findByServiceNameAndRuleTypeMatch(serviceName, ruleType, rules) != null;
  }
   
  
  
  @PUT
  @Path("/updateServiceList")
  public Response updateServiceList(ServiceListNew serviceListNew) {
      try {
          String serviceName = serviceListNew.getServiceName();
          String ruleType = serviceListNew.getRules().get(0).getRuleType();
  
          if (serviceName == null || ruleType == null) {
              throw new WebApplicationException(
                      "serviceName and ruleType must be provided",
                      Response.Status.BAD_REQUEST
              );
          }
  
          ServiceListNew existingService = serviceListRepo.findByServiceNameAndRuleType(
                  serviceName,
                  ruleType
          );
  
          if (existingService != null) {
              updateExistingService(existingService, serviceListNew);
              serviceListRepo.update(existingService);
              System.out.println("Updated service: " + existingService.getServiceName());
              
              return Response.ok(existingService).build();
          } else {
              throw new WebApplicationException(
                      "Service not found",
                      Response.Status.NOT_FOUND
              );
          }
      } catch (Exception e) {
          e.printStackTrace();
          return Response.serverError().build();
      }
  }
  

  private void updateExistingService(
    ServiceListNew existingService,
    ServiceListNew newService
  ) {
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
      System.out.println(
        "Updated metric rule for service: " + existingService.getServiceName()
      );
    } else if ("trace".equals(ruleType)) {
      Rule existingRule = existingService.getRules().get(0);
      Rule newRule = newService.getRules().get(0);
      existingRule.setDuration(newRule.getDuration());
      existingRule.setDurationConstraint(newRule.getDurationConstraint());
      existingRule.setStartDateTime(newRule.getStartDateTime());
      existingRule.setExpiryDateTime(newRule.getExpiryDateTime());
      System.out.println(
        "Updated trace rule for service: " + existingService.getServiceName()
      );
    } else if ("log".equals(ruleType)) {
      Rule existingRule = existingService.getRules().get(0);
      Rule newRule = newService.getRules().get(0);
      existingRule.setSeverityConstraint(newRule.getSeverityConstraint());
      existingRule.setSeverityText(newRule.getSeverityText());
      existingRule.setStartDateTime(newRule.getStartDateTime());
      existingRule.setExpiryDateTime(newRule.getExpiryDateTime());
      System.out.println(
        "Updated log rule for service: " + existingService.getServiceName()
      );
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
