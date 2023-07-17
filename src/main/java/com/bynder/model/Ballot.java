package com.bynder.model;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "BALLOT")
@Getter
@Setter
public class Ballot {

	@Indexed(unique = true)
	private String ballotId;

	private String lotteryId;

	private String userId;

	private Date createdDate;

}
