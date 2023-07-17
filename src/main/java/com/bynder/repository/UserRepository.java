package com.bynder.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bynder.model.User;

public interface UserRepository extends MongoRepository<User, Long> {

	User findByUserId(String userId);

	User findByUserName(String userName);

}
