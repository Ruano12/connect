package com.connect.connect.auth.login;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class LoginProcessorResponse {

  private HttpStatus status;
  private Map body;

  public LoginProcessorResponse(Map body, HttpStatus status) {
    super();
    this.status = status;
    this.body = body;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public Map getBody() {
    return body;
  }

  public void setBody(Map body) {
    this.body = body;
  }
}
