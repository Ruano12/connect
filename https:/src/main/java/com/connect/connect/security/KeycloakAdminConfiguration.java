package com.connect.connect.security;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.NotAuthorizedException;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author luan.gazin@oruspay.com.br
 * @year 2019
 */
@Configuration
@ConfigurationProperties(prefix = "admin.keycloak")
public class KeycloakAdminConfiguration {
  private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakAdminConfiguration.class);

  private String url;

  private String realm;

  private String clientId;

  private String username;

  private String password;

  private Integer poolSize;

  private boolean poolStart;

  private Integer readTimeout;

  private Integer connectionTimeout;

  @Bean
  public Keycloak getKeycloak() {
    LOGGER.info("[KEYCLOAK-ADMIN-CONFIGURATION] url: {}", url);
    LOGGER.info("[KEYCLOAK-ADMIN-CONFIGURATION] realm: {}", realm);
    LOGGER.info("[KEYCLOAK-ADMIN-CONFIGURATION] clientId: {}", clientId);
    LOGGER.info("[KEYCLOAK-ADMIN-CONFIGURATION] username: {}", username);
    LOGGER.info("[KEYCLOAK-ADMIN-CONFIGURATION] poolSize: {}", poolSize);
    LOGGER.info("[KEYCLOAK-ADMIN-CONFIGURATION] poolStart: {}", poolStart);

    Keycloak keycloak =
        KeycloakBuilder.builder()
            .serverUrl(url)
            .realm(realm)
            .grantType(OAuth2Constants.PASSWORD)
            .username(username)
            .password(password)
            .clientId(clientId)
            .resteasyClient(
                new ResteasyClientBuilder()
                    .connectionPoolSize(poolSize)
                    .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .build())
            .build();

    if (poolStart) {
      try {
        LOGGER.info("[KEYCLOAK-ADMIN-CONFIGURATION] starting ");
        AccessTokenResponse token = keycloak.tokenManager().grantToken();
        LOGGER.trace("[KEYCLOAK-ADMIN-CONFIGURATION] token type: {}", token.getTokenType());
        LOGGER.trace("[KEYCLOAK-ADMIN-CONFIGURATION] token: {}", token.getToken());
        LOGGER.info(
            "[KEYCLOAK-ADMIN-CONFIGURATION] token expires in: {}", token.getRefreshExpiresIn());
        LOGGER.trace("[KEYCLOAK-ADMIN-CONFIGURATION] token scope: {}", token.getScope());
        LOGGER.trace("[KEYCLOAK-ADMIN-CONFIGURATION] refresh token: {}", token.getRefreshToken());
        LOGGER.trace("[KEYCLOAK-ADMIN-CONFIGURATION] session state: {}", token.getSessionState());
      } catch (NotAuthorizedException e) {
        LOGGER.error("[KEYCLOAK-ADMIN-CONFIGURATION] erro ao obter token: {}", e.getMessage());
        LOGGER.error("[KEYCLOAK-ADMIN-CONFIGURATION] stack trace error: ", e);
      }
    }

    return keycloak;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getRealm() {
    return realm;
  }

  public void setRealm(String realm) {
    this.realm = realm;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getPoolSize() {
    return poolSize;
  }

  public void setPoolSize(Integer poolSize) {
    this.poolSize = poolSize;
  }

  public boolean isPoolStart() {
    return poolStart;
  }

  public void setPoolStart(boolean poolStart) {
    this.poolStart = poolStart;
  }

  public Integer getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(Integer readTimeout) {
    this.readTimeout = readTimeout;
  }

  public Integer getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(Integer connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }
}
