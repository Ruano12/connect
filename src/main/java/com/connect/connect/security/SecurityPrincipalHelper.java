package com.connect.connect.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;



@Component
public class SecurityPrincipalHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityPrincipalHelper.class);

  @Autowired private UserInfoOperations cachedUserInfoProvider;

  public Optional<String> getFullName() {
    Optional<String> optional = this.getAccessToken().map(AccessToken::getName);
    LOGGER.debug("[SECURITY-HELPER] getName: {}", optional);
    return optional;
  }

  public Optional<String> getFirstName() {
    Optional<String> optional = this.getAccessToken().map(AccessToken::getGivenName);
    LOGGER.debug("[SECURITY-HELPER] getName: {}", optional);
    return optional;
  }

  public Optional<String> getLastName() {
    Optional<String> optional = this.getAccessToken().map(AccessToken::getFamilyName);
    LOGGER.debug("[SECURITY-HELPER] getLastName: {}", optional);
    return optional;
  }

  public Optional<String> getEmail() {
    Optional<String> optional = this.getAccessToken().map(AccessToken::getEmail);
    LOGGER.debug("[SECURITY-HELPER] getEmail: {}", optional);
    return optional;
  }

  public String getUsername() {
    String userName =
        this.getAccessToken()
            .map(AccessToken::getSubject)
            .orElse("no-user");

    LOGGER.debug("[SECURITY-HELPER] getUsername: {}", userName);
    return userName;
  }

  public Optional<UUID> getPersonId() {
    Optional<UUID> optional =
        this.getOtherParameter(KeyCloakCustomFields.PERSON_ID)
            .map(String::valueOf)
            .map(UUID::fromString);

    LOGGER.debug("[SECURITY-HELPER] getOrganizationId: {}", optional);

    return optional;
  }

  public List<UUID> getOrganizationPersonIds() {

    List<UUID> orgs =
        this.getOtherParameter(KeyCloakCustomFields.ORGANIZATION_ID)
            .map(e -> (List<Object>) e)
            .stream()
            .flatMap(List::stream)
            .map(Object::toString)
            .map(UUID::fromString)
            .collect(Collectors.toList());
    LOGGER.debug("[SECURITY-HELPER] geOrganizationIds: {}", orgs);

    return orgs;
  }

  public List<UUID> getControllerPersonIds() {
    List<UUID> controllers =
        this.getOtherParameter(KeyCloakCustomFields.CONTROLLER_ID)
            .map(e -> (List<Object>) e)
            .stream()
            .flatMap(List::stream)
            .map(Object::toString)
            .map(UUID::fromString)
            .collect(Collectors.toList());

    LOGGER.debug("[SECURITY-HELPER] geControllerIds: {}", controllers);

    return controllers;
  }

  public List<UUID> getHoldingPersonIds() {
    List<UUID> holdins =
        this.getOtherParameter(KeyCloakCustomFields.HOLDING_ID)
            .map(e -> (List<Object>) e)
            .stream()
            .flatMap(List::stream)
            .map(Object::toString)
            .map(UUID::fromString)
            .collect(Collectors.toList());

    LOGGER.debug("[SECURITY-HELPER] geHoldingIdss: {}", holdins);

    return holdins;
  }

  public boolean isUserApplication() {
    return this.getOtherParameter(KeyCloakCustomFields.IS_APPLICATION)
        .map(String::valueOf)
        .map(Boolean::valueOf)
        .orElse(false);
  }

  public Optional<Object> getOtherParameter(KeyCloakCustomFields custom) {

    Optional<Object> optional =
        Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(KeycloakAuthenticationToken.class::cast)
            .map(KeycloakAuthenticationToken::getAccount)
            .map(OidcKeycloakAccount::getKeycloakSecurityContext)
            .map(KeycloakSecurityContext::getTokenString)
            .map(cachedUserInfoProvider::get)
            .map(m -> m.get(custom.getId()));

    LOGGER.debug(
        "[SECURITY-HELPER] getOtherParameter, key: {}: value: {}", custom.getId(), optional);
    return optional;
  }

  /** @return */
  private Optional<AccessToken> getAccessToken() {
    return this.getKeycloakPrincipal()
        .map(KeycloakPrincipal::getKeycloakSecurityContext)
        .map(KeycloakSecurityContext::getToken);
  }

  /** @return */
  private Optional<KeycloakPrincipal<KeycloakSecurityContext>> getKeycloakPrincipal() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .map(Authentication::getPrincipal)
        .map(KeycloakPrincipal.class::cast);
  }

  public boolean hasRole(String... rolesMatch) {
    List<String> matchesList = Arrays.asList(rolesMatch);
    LOGGER.debug("[SECURITY-HELPER] rolesMatch: {}", matchesList);

    if (matchesList == null || matchesList.isEmpty()) {
      return false;
    }

    Set<String> stream = new HashSet<>(matchesList);

    long matchesCount =
        Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getAuthorities)
            .stream()
            .flatMap(Collection::stream)
            .map(GrantedAuthority::getAuthority)
            .filter(stream::contains)
            .count();
    
    LOGGER.debug("[SECURITY-HELPER] matchesCount: {}", matchesCount);
    return matchesCount > 0;
  }
}
