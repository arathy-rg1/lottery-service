package com.bynder.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "SEQUENCES")
@Getter
@Setter
public class Sequences {
	
	@Indexed(unique=true)
	private String seqName;
	
	private long seqValue;

}
