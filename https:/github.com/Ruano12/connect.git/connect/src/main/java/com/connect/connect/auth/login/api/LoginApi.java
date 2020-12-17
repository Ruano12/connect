package com.connect.connect.auth.login.api;

import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.connect.connect.auth.login.LoginProcessor;
import com.connect.connect.auth.login.LoginProcessorResponse;

@RestController
@Transactional
public class LoginApi {
	
	@Autowired
	private LoginProcessor loginProcessor;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<Map> login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password){
		
		LoginProcessorResponse lps =
		        loginProcessor.validateLogin(username, password);
		
		return new ResponseEntity<>(lps.getBody(), lps.getStatus());
  	}
	
}
