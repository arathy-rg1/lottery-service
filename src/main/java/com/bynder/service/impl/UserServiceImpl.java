package com.bynder.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bynder.constants.Constants;
import com.bynder.dto.UserDTO;
import com.bynder.exception.EntityExistsException;
import com.bynder.model.User;
import com.bynder.repository.UserRepository;
import com.bynder.service.SequenceGeneratorService;
import com.bynder.service.UserService;

/**
 * Service class to handle the requests related to User operations:
 * 		* register a new user
 * 		* finds a user related to a userId
 * 
 * @author arathy
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	SequenceGeneratorService sequenceService;

	/**
	 * Registers a new user
	 * 
	 * If username is already present in the storage, appropriate error message is
	 * returned, else user is saved in the storage
	 * 
	 * @param userDto - input request containing user details like first name, last
	 *                name and user name
	 * 
	 * @return created userId
	 * 
	 * @throws EntityExistsException - exception thrown if username already exists
	 */
	public String registerUser(UserDTO userDto) throws EntityExistsException {

		User userEntity = userRepository.findByUserName(userDto.getUserName());
		if (userEntity != null) {
			throw new EntityExistsException("Username already exists!");
		}

		User user = mapUserEntity(userDto);
		userRepository.save(user);
		return String.valueOf(user.getUserId());

	}

	/**
	 * Maps the UserDTO object into User entity.
	 * 
	 * userId is generated from USER_ID_SEQUENCE
	 * 
	 * @param userDto - input UserDto request
	 * 
	 * @return mapped User entity
	 */
	private User mapUserEntity(UserDTO userDto) {

		User user = new User();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setUserName(userDto.getUserName());
		user.setUserId(String.valueOf(sequenceService.getNextSequenceNumber(Constants.USER_SEQUENCE)));

		return user;

	}

	/**
	 * Returns the user associated with a userId, if any present in storage.
	 * 
	 * @param userId - unique identifier of user
	 * 
	 * @return User associated with the requested id
	 * 
	 */
	public User findUser(String userId) {

		return userRepository.findByUserId(userId);
	}

}
