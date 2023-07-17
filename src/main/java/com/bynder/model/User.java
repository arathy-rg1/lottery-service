package com.bynder.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "USER")
@Getter
@Setter
public class User {

	@Indexed(unique = true)
	private String userId;

	private String userName;

	private String firstName;

	private String lastName;

}
