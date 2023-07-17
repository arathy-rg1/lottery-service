package com.bynder.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bynder.model.Ballot;

@Repository
public interface BallotRepository extends MongoRepository<Ballot, Long>{

	List<Ballot> findByUserId(String userId);

	List<Ballot> findByLotteryId(String lotteryId);
	
	List<Ballot> findByUserIdAndLotteryId(String userId, String lotteryId);
	
	Long countByLotteryId(String lotteryId);

	@Aggregation(pipeline = {
	        "{'$match':{'lotteryId':?0 }}",
	        "{'$sample':{size: 1}}"
	})
	Ballot findRandomBallot(String lotteryId);
	
}
