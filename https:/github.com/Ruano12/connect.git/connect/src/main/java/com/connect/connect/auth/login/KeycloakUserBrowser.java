package com.connect.connect.auth.login;

import java.util.Optional;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.connect.connect.auth.login.exceptions.UserNotFoundException;


/**
 * @author luan.gazin@oruspay.com.br
 * @year 2019
 */
@Component
public class KeycloakUserBrowser {

  @Autowired private KeycloakUserRepository userRepository;

  @Autowired private KeycloakGroupRepository keycloakGroupRepository;


  public UserRepresentation findById(
      @NotNull(message = "id required") String id) {

    return Optional.<UserRepresentation>of(
            userRepository.getUsersResource("keycloak-estudo").get(id).toRepresentation())
        .orElse(null);
  }

  
  public UserRepresentation findByUsername(
      @NotEmpty(message = "Informe o usuário") String username) {
    return userRepository
        .getUsersResource("keycloak-estudo")
        .search(username)
        .stream()
        .findFirst()
        .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
  }


  public UserResource findUserResourceById(
      @NotNull(message = "id required") String id) {

    return Optional.<UserResource>of(userRepository.getUsersResource("keycloak-estudo").get(id))
        .orElse(null);
  }
}
