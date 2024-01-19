package com.zaga.handler;

import com.zaga.entity.auth.UserCredentials;
import com.zaga.entity.auth.ServiceListNew;
import com.zaga.repo.AuthRepo;
import com.zaga.repo.ServiceListRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AuthCommandHandler {

  @Inject
  AuthRepo authRepo;

  @Inject
  ServiceListRepo serviceListRepo;

  public Response getUserInfo(final UserCredentials credentials) {
    try {
      UserCredentials userCreds = authRepo
          .find("username = ?1 and password = ?2", credentials.getUsername(), credentials.getPassword())
          .firstResult();
      if (userCreds != null) {
        return Response.status(200).entity(userCreds).build();
      } else {
        return Response
            .status(Response.Status.UNAUTHORIZED)
            .entity("Username or password is incorrect")
            .build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return Response.serverError().build();
    }
  }

  public Response saveUserInfo(final UserCredentials credentials) {
    System.out.println("----------------Saving user info just enttered---------------");
    try {
      UserCredentials userCreds = authRepo
          .find("username = ?1", credentials.getUsername())
          .firstResult();
      if (userCreds == null) {
        System.out.println("userCredentials--------------------"+userCreds);
        authRepo.persist(credentials);
        return Response
            .status(201)
            .entity("User registered successfully")
            .build();
      } else {
        return Response
            .status(Response.Status.NOT_FOUND)
            .entity("User Already exists")
            .build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public UserCredentials getUserInfoByUsername(String username) {
    return authRepo.find("username", username).firstResult();
  }
  // public static void updateUserPassword(String username, String newPassword) {
  // update("username", username)
  // .set("password", newPassword)
  // .execute();
  // }

  // public Response addServiceList(final ServiceList serviceList) {
  // try {
  // ServiceList serviceData = serviceListRepo
  // .find("serviceName = ?1", serviceList.getServiceName())
  // .firstResult();
  // // System.out.println("------Register service---- " +
  // serviceData.getServiceName());
  // if (serviceData == null) {
  // serviceListRepo.persist(serviceList);
  // return Response
  // .status(201)
  // .entity("Service registered successfully")
  // .build();
  // } else {
  // return Response
  // .status(Response.Status.NOT_FOUND)
  // .entity("Service Already exists")
  // .build();
  // }
  // } catch (Exception e) {
  // e.printStackTrace();
  // return Response.status(501).entity(e).build();
  // }
  // }

  public Response getServiceList(final UserCredentials userCredentials) {
    try {
      java.util.List<ServiceListNew> serviceData = serviceListRepo
          .find("roles in ?1", userCredentials.getRoles())
          .list();
          System.out.println("serviceData--------"+serviceData);
      if (serviceData != null) {
        return Response.status(201).entity(serviceData).build();
      } else {
        return Response
            .status(Response.Status.NOT_FOUND)
            .entity("Data not found for this role")
            .build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  // public UserCredentials getUserInfoByUsername(String username) {
  // return authRepo.find("username", username).firstResult();
  // }

  public UserCredentials updateUserCredentials(UserCredentials userCredentials) {
    // UserCredentials userData = authRepo.getUser(userCredentials.getUsername());
    // System.out.println(userData);
    UserCredentials existingUser = authRepo.getUser(userCredentials.getUsername());
    if (existingUser == null) {
      throw new WebApplicationException("User Not Found ", 500);
    }
    UserCredentials user = userCredentials;
    System.out.println(user);
    // user.setId(existingUser.getId());
    user.setRoles(existingUser.getRoles());
    user.setUsername(existingUser.getUsername());
    UserCredentials.update(user);
    System.out.println(user);
    return userCredentials;
  }




  
}
