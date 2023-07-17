package com.bynder.service;

import com.bynder.dto.UserDTO;
import com.bynder.exception.EntityExistsException;
import com.bynder.model.User;

public interface UserService {

	String registerUser(UserDTO userDto) throws EntityExistsException;

	User findUser(String userId);
}
