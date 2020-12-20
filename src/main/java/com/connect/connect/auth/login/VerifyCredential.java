package com.connect.connect.auth.login;

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.connect.connect.auth.login.exceptions.InvalidCredentialsException;
import com.connect.connect.auth.login.exceptions.TechnicalException;

@Component
public class VerifyCredential {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyCredential.class);
	
	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	@Value("${keycloak.realm}")
	private String reaml;

	@Value("${login.external-login-resource}")
	private String resource;
	
	@Autowired private RestTemplate rest;
	
	private final String INVALID_USER_CREDENTIALS = "Invalid user credentials";
	
	private final String INVALID_GRANT = "invalid_grant";

	public void validateCredentials(String username, String password) {
		MultiValueMap<String, String> authbody = new LinkedMultiValueMap<String, String>();
		
		authbody.add("grant_type", "password");
	    authbody.add("client_id", "login-app");
	    authbody.add("username", username);
	    authbody.add("password",password);
		
		RequestEntity requestToken =
		          RequestEntity.post(
		                  URI.create(authServerUrl + "/realms/" + reaml + "/protocol/openid-connect/token"))
		              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		              .accept(MediaType.APPLICATION_JSON)
		              .body(authbody, String.class);

		ResponseEntity respToken = rest.exchange(requestToken, Map.class);
  
		if (respToken.getStatusCode().isError()) {
			LOGGER.warn("Keycloak retornou erro no login");
			Map body = (Map) respToken.getBody();
			if (INVALID_GRANT.equals(body.get("error"))
			        && INVALID_USER_CREDENTIALS.equals(body.get("error_description"))) {
				throw new InvalidCredentialsException("Login e senha invalido");
		    }else {
		    	throw new TechnicalException((String) body.get("error_description"));
		    }
		}
	}
}
