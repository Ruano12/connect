package com.connect.connect.auth.login;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class KeycloakUserRepository {

  @Autowired 
  private KeycloakRealmRepository realmRepository;

  public UsersResource getUsersResource(String realm) {
    return realmRepository.getRealmResource(realm).users();
  }
}
