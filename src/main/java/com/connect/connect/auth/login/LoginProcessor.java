package com.connect.connect.auth.login;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.connect.connect.auth.login.exceptions.AccountDisabledException;
import com.connect.connect.auth.login.exceptions.InvalidCredentialsException;
import com.connect.connect.auth.login.exceptions.LoginWaitingApprovalException;
import com.connect.connect.auth.login.exceptions.UsernameNotFoundException;
import com.connect.connect.auth.login.exceptions.ValidationEmailPendingException;
import com.connect.connect.login.enums.CustomerStatus;

@Component
public class LoginProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginProcessor.class);
	
	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	@Value("${keycloak.realm}")
	private String reaml;

	@Value("${login.external-login-resource}")
	private String resource;
	
	@Autowired 
	private KeycloakUserBrowser keycloakUserBrowser;
	
	@Autowired 
	private RestTemplate rest;	
	
	public LoginProcessorResponse validateLogin(String username, String password) {
		MultiValueMap<String, String> authbody = new LinkedMultiValueMap<String, String>();
		
		authbody.add("grant_type", "password");
	    authbody.add("client_id", "login-app");
	    authbody.add("username", username);
	    authbody.add("password", password);
	    
	    LoginProcessorResponse lps;
	    
	    try {
	    	LOGGER.info("Chamando operação de login no keycloak...");
	    	RequestEntity requestToken =
	    	          RequestEntity.post(
	    	                  URI.create(authServerUrl + "/realms/" + reaml + "/protocol/openid-connect/token"))
	    	              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    	              .accept(MediaType.APPLICATION_JSON)
	    	              .body(authbody, String.class);
	    	
	    	 ResponseEntity respToken = rest.exchange(requestToken, Map.class);
	         String accessToken = null;
	         
	         if (respToken.getStatusCode().isError()) {
	        	 LOGGER.warn("Keycloak retornou erro no login");
	             tryConvertToException((Map) respToken.getBody());
	         }
	         
	         lps = new LoginProcessorResponse((Map) respToken.getBody(), respToken.getStatusCode());
	         accessToken = (String) lps.getBody().get("access_token");
	         
	         LOGGER.info("Chamando operação de userinfo no keycloak...");

	         RequestEntity requestUserInfo =
	             RequestEntity.post(
	                     URI.create(
	                         authServerUrl + "/realms/" + reaml + "/protocol/openid-connect/userinfo"))
	                 .accept(MediaType.APPLICATION_JSON)
	                 .header("Authorization", "Bearer " + accessToken)
	                 .body(null);
	         
	         ResponseEntity respUserInfo = rest.exchange(requestUserInfo, Map.class);
	         Map mapUserInfo = (Map) respUserInfo.getBody();
	         
	         if (respUserInfo.getStatusCode().isError()) {
	        	 LOGGER.error("Keycloak retornou erro no userinfo");
	             tryConvertToException(mapUserInfo);
	         }
	         
	      // Se for login de usuário, busca informações do usuário para validar os atributos
	         if ("password".equals(authbody.getFirst("grant_type"))) {

	           String userId = (String) mapUserInfo.get("sub");

	           LOGGER.debug("Buscando informações do usuário {} no realm {}...", userId, reaml);
	           UserRepresentation userRepresentation = keycloakUserBrowser.findById(userId);

	           if (userRepresentation == null) {
	             throw new UsernameNotFoundException("Usuário não encontrado");
	           }

	           if (userRepresentation.getAttributes() != null) {
	             List<String> category = userRepresentation.getAttributes().get("category");
	             if (category != null && !category.isEmpty()) {
	               if (UserGroup.CUSTOMER.toString().equals(category.get(0))) {

	                 List<String> customerStatus =
	                     userRepresentation.getAttributes().get("customer-status");
	                 if (customerStatus != null && !customerStatus.isEmpty()) {
	                   if (CustomerStatus.BLOCKED.toString().equals(customerStatus.get(0).toString())) {
	                     throw new AccountDisabledException("Usuário bloqueado");
	                   } else if (CustomerStatus.INACTIVE
	                       .toString()
	                       .equals(customerStatus.get(0).toString())) {
	                     throw new AccountDisabledException("Usuário inativo");
	                   } else if (CustomerStatus.WAITING_APPROVAL
	                       .toString()
	                       .equals(customerStatus.get(0).toString())) {
	                     throw new LoginWaitingApprovalException("Usuário aguardando aprovação");
	                   }
	                 } else {
	                   LOGGER.warn(
	                       "O usuário {} não possui atributo de customer-status, aceitando...", username);
	                 }
	               }

	             } else {
	               LOGGER.warn(
	                   "O usuário {} não possui atributo de category, aceitando...", username);
	             }
	           } else {
	             LOGGER.warn("O usuário {} não possui atributos, aceitando...", username);
	           }
	         }
	         
	         LOGGER.info("Login realizado com sucesso...");

	         return lps;
	    }catch (Exception e) {
	    	LOGGER.error("Erro ao conectar no keycloak", e);
	        throw e;
		}
	}
	
	private void tryConvertToException(Map body) {

	    if ("invalid_grant".equals(body.get("error"))
	        && "Account is not fully set up".equals(body.get("error_description"))) {
	      throw new ValidationEmailPendingException((String) body.get("error_description"));
	    } else if ("invalid_grant".equals(body.get("error"))
	        && "Invalid user credentials".equals(body.get("error_description"))) {
	      throw new InvalidCredentialsException((String) body.get("error_description"));
	    } else if ("invalid_grant".equals(body.get("error"))
	        && "Account disabled".equals(body.get("error_description"))) {
	      throw new AccountDisabledException((String) body.get("error_description"));
	    } else {
	      throw new AccessDeniedException((String) body.get("error_description"));
	    }
	  }
}
