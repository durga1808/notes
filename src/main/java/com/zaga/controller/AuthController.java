package com.zaga.controller;

import com.zaga.entity.auth.UserCredentials;
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
