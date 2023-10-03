package com.zaga.handler;

import com.zaga.entity.auth.UserCredentials;
import com.zaga.repo.AuthRepo;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.vertx.mutiny.ext.auth.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AuthCommandHandler {

    @Inject
    AuthRepo authRepo;

    public Response getUserInfo(final UserCredentials credentials) {
    try {
      UserCredentials userCreds = authRepo
        .find("username = ?1", credentials.getUsername())
        .firstResult();
      if (userCreds != null) {
        if (userCreds.getPassword().equals(credentials.getPassword())) {
          if (userCreds.getRoles().containsAll(credentials.getRoles())) {
            return Response.status(200).entity(userCreds).build();
          } else {
            return Response
              .status(Response.Status.FORBIDDEN)
              .entity("You don't have valid permission")
              .build();
          }
        } else {
          return Response
            .status(Response.Status.UNAUTHORIZED)
            .entity("Username or password is incorrect")
            .build();
        }
      } else {
        return Response
          .status(Response.Status.NOT_FOUND)
          .entity("User not found")
          .build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public Response saveUserInfo(final UserCredentials credentials) {
    try {
      UserCredentials userCreds = authRepo
        .find("username = ?1", credentials.getUsername())
        .firstResult();
      if (userCreds == null) {
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


//   public UserCredentials getUserInfoByUsername(String username) {
//     return authRepo.find("username", username).firstResult();
// }

  public UserCredentials updateUserCredentials(UserCredentials userCredentials){
    // UserCredentials userData = authRepo.getUser(userCredentials.getUsername());
    // System.out.println(userData);
    UserCredentials existingUser = authRepo.getUser(userCredentials.getUsername());
    if (existingUser == null) {
          throw new WebApplicationException("User Not Found ", 500);
    }
    UserCredentials user = userCredentials;
    System.out.println(user);
    user.setId(existingUser.getId());
    user.setRoles(existingUser.getRoles());
    user.setUsername(existingUser.getUsername());
    UserCredentials.update(user);
    System.out.println(user);
    return userCredentials;   
  }
}
    
