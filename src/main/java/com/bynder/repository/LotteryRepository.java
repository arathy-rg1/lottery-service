package com.bynder.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.bynder.model.Lottery;

@Repository
public interface LotteryRepository extends MongoRepository<Lottery, Long> {

	List<Lottery> findByStatus(String status);

	Lottery findByLotteryId(String lotteryId);

	@Query("{ 'lotteryId' : ?0 }")
	@Update("{ '$set' : { 'endDate' : ?1, 'winnerBallot' : ?2, 'status' : ?3 } }")
	void updateLottery(String lotteryId, Date endDate, String winnerBallot, String status);

}
