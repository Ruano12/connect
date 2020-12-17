package com.connect.connect.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;


/**
 * Essa classe é responsável por obter e unificar as roles fazendo leitura do token e do UserInfo.
 *
 * @author ricardo.marques@oruspay.com.br
 */
@Component
public class UserInfoKeycloakAuthenticationProvider implements AuthenticationProvider {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(UserInfoKeycloakAuthenticationProvider.class);

  @Autowired 
  private UserInfoOperations cachedUserInfoProvider;

  private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  public void setGrantedAuthoritiesMapper(GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
    this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
    List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

    addTokenRoles(token, grantedAuthorities);
    addUserInfoRoles(token, grantedAuthorities);

    return new KeycloakAuthenticationToken(
        token.getAccount(), token.isInteractive(), mapAuthorities(grantedAuthorities));
  }

  private void addTokenRoles(
      KeycloakAuthenticationToken token, List<GrantedAuthority> grantedAuthorities) {
    for (String role : token.getAccount().getRoles()) {
      grantedAuthorities.add(new KeycloakRole(role));
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void addUserInfoRoles(
      KeycloakAuthenticationToken token, List<GrantedAuthority> grantedAuthorities) {
    KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal =
        (KeycloakPrincipal) token.getPrincipal();
    String idToken = principal.getKeycloakSecurityContext().getTokenString();
    String resourceName = principal.getKeycloakSecurityContext().getDeployment().getResourceName();

    LOGGER.debug("Obtendo userinfo do cache...");

    Map node = null;
    try {
      node = cachedUserInfoProvider.get(idToken);
    } catch (RuntimeException e) {
      LOGGER.error("Não foi possível obter dados do token no REDIS.",e);
      throw new InvalidAccessException(e);
    }

    if (node != null) {
      if (node.containsKey("resources")) {
        node = (Map) node.get("resources");
        if (node.containsKey(resourceName)) {
          node = (Map) node.get(resourceName);
          if (node.containsKey("roles")) {
            String roles[] = new String[] {};
            if (node.get("roles") != null && node.get("roles").getClass().isArray()) {
              roles = (String[]) node.get("roles");
            } else if (node.get("roles") != null
                && Collection.class.isInstance(node.get("roles"))) {
              Collection colNodes = (Collection) node.get("roles");
              roles = (String[]) colNodes.toArray(new String[colNodes.size()]);
            } else {
              roles = new String[] {(String) node.get("roles")};
            }

            LOGGER.debug("Adicionando {} roles do UserInfo...", roles.length);
            for (String role : roles) {
              grantedAuthorities.add(new KeycloakRole(role));
            }
          }
        }
      }
    }
  }

  private Collection<? extends GrantedAuthority> mapAuthorities(
      Collection<? extends GrantedAuthority> authorities) {
    return grantedAuthoritiesMapper != null
        ? grantedAuthoritiesMapper.mapAuthorities(authorities)
        : authorities;
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return KeycloakAuthenticationToken.class.isAssignableFrom(aClass);
  }
}
