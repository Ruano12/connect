package com.connect.connect.auth.login;

import org.keycloak.admin.client.resource.GroupsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class KeycloakGroupRepository {
	
  @Autowired 
  private KeycloakRealmRepository realmRepository;

  public GroupsResource getGroupResource() {
    return realmRepository.getRealmResource().groups();
  }
}
