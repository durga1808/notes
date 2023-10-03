package com.zaga.controller;


import com.zaga.entity.auth.UserCredentials;
import com.zaga.handler.AuthCommandHandler;
import com.zaga.repo.AuthRepo;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/AuthController")
public class AuthController {


    @Inject
    AuthCommandHandler authCommandHandler;

    @Inject
    AuthRepo repo;

    @POST
    @Path("/login")
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

