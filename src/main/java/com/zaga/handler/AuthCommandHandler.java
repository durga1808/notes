package com.zaga.handler;

import com.zaga.entity.auth.UserCredentials;
import com.zaga.repo.AuthRepo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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


  public UserCredentials getUserInfoByUsername(String username) {
    return authRepo.find("username", username).firstResult();
}
// public static void updateUserPassword(String username, String newPassword) {
//     update("username", username)
//         .set("password", newPassword)
//         .execute();
// }

}
    
