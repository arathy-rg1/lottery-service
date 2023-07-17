package com.bynder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bynder.dto.UserDTO;
import com.bynder.exception.EntityExistsException;
import com.bynder.service.UserService;

/**
 * Controller class to handle the requests related to User operations: 
 * 		*register a new user
 * 
 * @author arathy
 *
 */
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * Registers a new user
	 * 
	 * If username is already present in the storage, appropriate error message is
	 * returned, else user is saved in the storage
	 * 
	 * @param userDto - input request containing user details like first name, last
	 *                name and user name
	 * 
	 * @return success response with created userId
	 * 
	 * @throws EntityExistsException - exception thrown if username already exists
	 */
	@PostMapping(value = "/register")
	public String registerUser(@RequestBody(required = true) UserDTO userDto) throws EntityExistsException {

		String userId = userService.registerUser(userDto);
		return "User registered successfully with userID:" + userId;

	}

}
