package com.connect.connect.security;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BeanKeycloakConfiguration {
	
	@Bean
	@Primary
	public KeycloakSpringBootConfigResolver keycloakConfigResolver(
			KeycloakSpringBootProperties properties) {
		return new KeycloakSpringBootConfigResolver();
	}

}
