package com.bynder.service;

import java.util.Date;
import java.util.List;

import com.bynder.dto.LotteryDTO;
import com.bynder.dto.LotteryResultDTO;
import com.bynder.exception.LotteryStatusException;
import com.bynder.exception.ResourceNotFoundException;
import com.bynder.model.Lottery;

public interface LotteryService {

	List<LotteryDTO> getLotteries(String status) throws ResourceNotFoundException;

	LotteryResultDTO getLotteryResult(String lotteryId) throws ResourceNotFoundException, LotteryStatusException;

	String createLottery(LotteryDTO lotteryDto);

	Lottery getLottery(String lotteryId);

	List<Lottery> getActiveLotteries();

	void closeLottery(String lotteryId, Date endDate, String winnerBallot, String status);

}
