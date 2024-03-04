package com.zaga.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;

import com.zaga.entity.auth.Environments;
import com.zaga.entity.auth.Rule;
import com.zaga.entity.auth.ServiceListNew;
import com.zaga.entity.auth.UserCredentials;
import com.zaga.handler.AuthCommandHandler;
import com.zaga.repo.AuthRepo;
import com.zaga.repo.ServiceListRepo;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Path("/AuthController")
public class AuthController {

    @Inject
    AuthCommandHandler authCommandHandler;

    @Inject
    AuthRepo repo;

    @Inject
    ServiceListRepo serviceListRepo;

    @Inject
    MongoClient mongoClient;

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
    // try {
    // Response response = authCommandHandler.getClusterInfo(credentials);
    // return response;
    // } catch (Exception e) {
    // e.printStackTrace();
    // return Response.serverError().build();
    // }
    // }

    // @POST
    // @Path("/register")
    // public Response saveUserData(final UserCredentials credentials) {
    // try {
    // Response response = authCommandHandler.saveUserInfo(credentials);
    // return response;
    // } catch (Exception e) {
    // e.printStackTrace();
    // return Response.serverError().build();
    // }
    // }

    @PUT
    @Path("/clusterDataUpdate")
    public Response updateUserData(final UserCredentials credentials) {
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
    // try {
    // Response response = authCommandHandler.addServiceList(serviceList);
    // return response;
    // } catch (Exception e) {
    // e.printStackTrace();
    // return Response.serverError().build();
    // }
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
                            .entity("Service with serviceName '" + serviceName + "' and ruleType '" + ruleType
                                    + "' already exists.")
                            .build();
                }

                // Check if a service with the same serviceName exists
                ServiceListNew existingService = serviceListRepo.findByServiceName(serviceName);

                if (existingService != null) {
                    // Check if the existing service already has "log", "trace", and "metrics" rule
                    // types
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

    // Additional method to check if the existing service already has "log",
    // "trace", and "metrics" rule types
    private boolean hasLogTraceMetricsRuleTypes(ServiceListNew existingService) {

        List<Rule> existingRules = existingService.getRules();

        // Collect unique rule types from existing rules
        Set<String> uniqueRuleTypes = existingRules.stream()
                .map(Rule::getRuleType)
                .collect(Collectors.toSet());

        // Check if "log", "trace", and "metrics" rule types are present
        return uniqueRuleTypes.contains("log") && uniqueRuleTypes.contains("trace")
                && uniqueRuleTypes.contains("metrics");
    }

    private boolean isServiceAlreadyExists(String serviceName, String ruleType, List<Rule> rules) {
        return serviceListRepo.findByServiceNameAndRuleTypeMatch(serviceName, ruleType, rules) != null;
    }

    @PUT
    @Path("/updateServiceList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateServiceList(ServiceListNew serviceListNew) {
        try {
            String serviceName = serviceListNew.getServiceName();

            if (serviceName == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"serviceName must be provided\"}")
                        .build();
            }

            ServiceListNew existingService = serviceListRepo.findByServiceName(serviceName);

            if (existingService != null) {
                updateExistingService(existingService, serviceListNew);
                serviceListRepo.update(existingService);
                System.out.println("Updated service: " + existingService.getServiceName());

                // Return the updated service data in JSON format
                return Response.ok(existingService, MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Service not found\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    private void updateExistingService(ServiceListNew existingService, ServiceListNew serviceListNew) {
        List<Rule> newRules = serviceListNew.getRules();

        for (Rule newRule : newRules) {
            String ruleType = newRule.getRuleType();
            Rule existingRule = findRuleByType(existingService, ruleType);

            if (existingRule != null) {
                switch (ruleType) {
                    case "metric":
                        updateMetricRule(existingRule, newRule);
                        break;
                    case "trace":
                        updateTraceRule(existingRule, newRule);
                        break;
                    case "log":
                        updateLogRule(existingRule, newRule);
                        break;
                    default:
                        System.out.println("Not matched rule type: " + ruleType);
                        break;
                }
            } else {
                System.out.println("Rule not found for service: " + existingService.getServiceName() + " and ruleType: "
                        + ruleType);
            }
        }
    }

    private Rule findRuleByType(ServiceListNew existingService, String ruleType) {
        for (Rule rule : existingService.getRules()) {
            if (ruleType.equals(rule.getRuleType())) {
                return rule;
            }
        }
        return null;
    }

    private void updateMetricRule(Rule existingRule, Rule newRule) {
        existingRule.setMemoryConstraint(newRule.getMemoryConstraint());
        existingRule.setMemoryLimit(newRule.getMemoryLimit());
        existingRule.setCpuConstraint(newRule.getCpuConstraint());
        existingRule.setCpuLimit(newRule.getCpuLimit());
        existingRule.setStartDateTime(newRule.getStartDateTime());
        existingRule.setExpiryDateTime(newRule.getExpiryDateTime());
        existingRule.setCpuAlertSeverityText(newRule.getCpuAlertSeverityText());
        existingRule.setMemoryAlertSeverityText(newRule.getMemoryAlertSeverityText());
        System.out.println("Updated metric rule");
    }

    private void updateTraceRule(Rule existingRule, Rule newRule) {
        existingRule.setDuration(newRule.getDuration());
        existingRule.setDurationConstraint(newRule.getDurationConstraint());
        existingRule.setStartDateTime(newRule.getStartDateTime());
        existingRule.setExpiryDateTime(newRule.getExpiryDateTime());
        existingRule.setTracecAlertSeverityText(newRule.getTracecAlertSeverityText());
        System.out.println("Updated trace rule");
    }

    private void updateLogRule(Rule existingRule, Rule newRule) {
        existingRule.setSeverityConstraint(newRule.getSeverityConstraint());
        existingRule.setSeverityText(newRule.getSeverityText());
        existingRule.setStartDateTime(newRule.getStartDateTime());
        existingRule.setExpiryDateTime(newRule.getExpiryDateTime());
        existingRule.setLogAlertSeverityText(newRule.getLogAlertSeverityText());
        System.out.println("Updated log rule");
    }

    @POST
    @Path("/getServiceList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceList(final UserCredentials userCredentials) {
        try {
            Response response = authCommandHandler.getServiceList(userCredentials);
            return response;
        } catch (Exception e) {
            System.out.println("ERRORRR:---------------------------- " + e.getMessage());
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
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }

    // @DELETE
    // @Path("/{clusterUsername}/delete-environments/{clusterId}")
    // public Response deleteEnvironment(
    // @QueryParam("clusterUsername") String clusterUsername,
    // @QueryParam("clusterId") long clusterId) {

    // System.out.println("---------------------[CLUSTER USER NAME]------------- " +
    // clusterUsername);
    // System.out.println("---------------------[CLUSTER ID]------------- " +
    // clusterId);
    // UserCredentials userCredentials =
    // repo.findByClusterUsername(clusterUsername);
    // System.out.println("---------------[USER CREDS]-------------- " +
    // userCredentials);

    // if (userCredentials == null) {
    // return Response.status(Response.Status.NOT_FOUND).build();
    // }

    // List<Environments> environments = userCredentials.getEnvironments();

    // environments.removeIf(env -> env.getClusterId() == clusterId);
    // userCredentials.setEnvironments(environments);

    // try {
    // repo.update(userCredentials);
    // return Response.ok().build();
    // } catch (Exception e) {
    // e.printStackTrace();
    // return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    // }
    // }

    @DELETE
    @Path("/delete-environments")
    public Response deleteEnvironment(
            @QueryParam("userName") String userName,
            @QueryParam("clusterId") long clusterId) {

        System.out.println("---------------------[CLUSTER USER NAME]------------- " + userName);
        System.out.println("---------------------[CLUSTER ID]------------- " + clusterId);

        if (userName == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            // repo.update({username:"admin"},{$pull : {environments:{clusterId:3}}});
            MongoDatabase database = mongoClient.getDatabase("ObservabilityCredentials");
            MongoCollection<Document> collection = database.getCollection("UserCreds");
            // Specify the query condition (username: "admin")
            Document query = new Document("username", userName);

            // Specify the update operation (pulling environments with clusterId: 3)
            Document update = new Document("$pull", new Document("environments", new Document("clusterId", clusterId)));

            collection.updateOne(query, update);
            System.out.println("---------[SUCCESSFULLY DELETED ENVIRONMENT]-----------");
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }




    @PUT
    @Path("/clusterStatusUpdate")
    public Response updateClusterStatus(@QueryParam("userName") String userName,
    @QueryParam("clusterId") long clusterId,
    @QueryParam("clusterStatus") String clusterStatus) {
        if (userName == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            // repo.update({username:"admin"},{$pull : {environments:{clusterId:3}}});
            MongoDatabase database = mongoClient.getDatabase("ObservabilityCredentials");
            MongoCollection<Document> collection = database.getCollection("UserCreds");
            
            // Specify the filter to match the document
            Document filter = new Document("username", userName)
                    .append("environments", new Document("$elemMatch", new Document("clusterId", clusterId)));

            // Specify the update operation
            Document update = new Document("$set", new Document("environments.$.clusterStatus", clusterStatus));

            collection.updateOne(filter, update);
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
