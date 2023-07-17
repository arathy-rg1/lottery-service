package com.bynder.service;

import java.util.List;

import com.bynder.dto.BallotDTO;
import com.bynder.exception.LotteryStatusException;
import com.bynder.exception.ResourceNotFoundException;

public interface BallotService {

	List<BallotDTO> getBallots(String userId, String lotteryId) throws ResourceNotFoundException;

	String createBallot(BallotDTO ballotDto) throws ResourceNotFoundException, LotteryStatusException;

	String getLotteryWinner(String lotteryId);

}
