package com.connect.connect.security;

public enum KeyCloakCustomFields {
	  PERSON_ID("person-id"),
	  ORGANIZATION_ID("organization-person-id"),
	  CONTROLLER_ID("controller-person-id"),
	  HOLDING_ID("holding-person-id"),
	  IS_APPLICATION("is-application");

	  private String id;

	  private KeyCloakCustomFields(String id) {
	    this.id = id;
	  }

	  public String getId() {
	    return this.id;
	  }
	}
