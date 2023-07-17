package com.bynder.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.bynder.constants.Constants;
import com.bynder.model.Sequences;
import com.bynder.service.SequenceGeneratorService;

/**
 * Service class to handle sequence generation
 * 
 * @author arathy
 *
 */
@Service
public class SequenceGeneratorServiceImpl implements SequenceGeneratorService {

	@Autowired
	private MongoOperations mongoOperations;

	/**
	 * Returns next sequence number for given sequence name.
	 * 
	 * Increments sequence number if sequence name is present in storage, else
	 * creates a new sequence with starting value as 1.
	 * 
	 * @param sequenceName - sequence name for which next sequence number is
	 *                     requested
	 * 
	 * @return next sequence number
	 * 
	 */
	public long getNextSequenceNumber(String sequenceName) {

		Sequences sequence = mongoOperations.findAndModify(
				new Query(Criteria.where(Constants.SEQ_NAME).is(sequenceName)),
				new Update().inc(Constants.SEQ_VALUE, 1), new FindAndModifyOptions().returnNew(true).upsert(true),
				Sequences.class);

		return sequence == null?1:sequence.getSeqValue();

	}

}
