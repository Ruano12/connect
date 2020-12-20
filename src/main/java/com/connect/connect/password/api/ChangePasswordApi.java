package com.connect.connect.password.api;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.connect.connect.password.PasswordUtils;

@RestController
@Transactional
public class ChangePasswordApi {

	@Autowired
	private PasswordUtils passwordUtils;
	
	@RequestMapping(value = "/change-password", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public HttpEntity<Void> login(@RequestParam(value = "oldpassword") String oldPassword, @RequestParam(value = "newpassword") String newPassword){
		passwordUtils.changePassword(oldPassword, newPassword);
		
		return new ResponseEntity<Void>( HttpStatus.OK );
	}
}
