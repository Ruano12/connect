package com.connect.connect.password;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.connect.connect.auth.login.KeycloakUserBrowser;
import com.connect.connect.auth.login.VerifyCredential;
import com.connect.connect.enums.UserRequiredActions;
import com.connect.connect.security.SecurityPrincipalHelper;

@Component
public class PasswordUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PasswordUtils.class);
	
	@Autowired 
	private KeycloakUserBrowser userBrowser;
	
	@Autowired
	private SecurityPrincipalHelper securityPrincipalHelper;
	
	@Autowired
	private VerifyCredential verifyCredential;
	
	public void resetPassword(String userId, String newPassword) {
		
		UserResource userResource = userBrowser.findUserResourceById(userId);
		CredentialRepresentation passwordCred = new CredentialRepresentation();
	    passwordCred.setTemporary(false);
	    passwordCred.setType(CredentialRepresentation.PASSWORD);
	    passwordCred.setValue(newPassword);
	    try {
	        userResource.resetPassword(passwordCred);
	    } catch (Exception e) {
	    	LOGGER.error("[PASSWORD UTILS] Resetando password {}", userId);
	    }
	    
	    UserRepresentation userRepresentation = userResource.toRepresentation();
	    List<String> requiredActions = userRepresentation.getRequiredActions();
	    
	    if (requiredActions != null && !requiredActions.isEmpty()) {
	        requiredActions.remove(UserRequiredActions.VERIFY_EMAIL.name());
	        requiredActions.remove(UserRequiredActions.UPDATE_PASSWORD.name());
	        userRepresentation.setRequiredActions(requiredActions);
	    }
	    
	    userRepresentation.setEmailVerified(true);
	    userResource.update(userRepresentation);    
	}
	
	public void changePassword(String oldPassword, String newPassword) {
		String id = securityPrincipalHelper.getUsername();
		
		UserResource userResource = userBrowser.findUserResourceById(id);
		
		CredentialRepresentation credentials = userResource.credentials().stream()
		         .findFirst()
		         .orElse(null);
		
		String username = userResource.toRepresentation().getUsername();
		verifyCredential.validateCredentials(username, oldPassword);
		  
		this.resetPassword(id, newPassword);
	}
}
