package com.connect.connect.auth.login;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeycloakRealmRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakRealmRepository.class);

  @Autowired 
  private Keycloak keycloak;

  public RealmResource getRealmResource() {
    return getRealmResource("keycloak-estudo");
  }

  public RealmResource getRealmResource(String realm) {

    LOGGER.info("[REALM-REPOSITORY-IMPL] getRealmResource: {}", realm);
    RealmResource realmResource = keycloak.realm(realm);
    LOGGER.trace("[REALM-REPOSITORY-IMPL] realmResource: {}", realmResource);
    return realmResource;
  }
}
