package com.bynder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bynder.dto.LotteryDTO;
import com.bynder.dto.LotteryResultDTO;
import com.bynder.exception.LotteryStatusException;
import com.bynder.exception.ResourceNotFoundException;
import com.bynder.service.LotteryService;

/**
 * Controller class to handle the requests related to Lottery operations: 
 * 		* get all the lottery present in storage. 
 * 		* get the lottery list based on status(open/closed). 
 * 		* save a new lottery into the storage.
 * 
 * @author arathy
 *
 */
@RestController
public class LotteryController {

	@Autowired
	private LotteryService lotteryService;

	/**
	 * Returns the list of lotteries present in the storage.
	 * 
	 * Takes an optional parameter status(value can be OPEN/CLOSED) based on which
	 * list of open/closed lottery list is returned.
	 * 
	 * If status is not present in request, returns all the lotteries in the
	 * storage.
	 * 
	 * @param status - optional input parameter(value can be OPEN/CLOSED)
	 * 
	 * @return list of lotteries in the storage based on input request
	 * 
	 * @throws ResourceNotFoundException - exception thrown if no lotteries based on
	 *                                   the request is present in storage
	 */
	@GetMapping(value = "/lotteries")
	public List<LotteryDTO> getLotteries(@RequestParam(value = "status", required = false) String status)
			throws ResourceNotFoundException {

		return lotteryService.getLotteries(status);

	}

	/**
	 * Returns the lottery result for a particular lotteryId.
	 * 
	 * Only returns the results for closed lotteries.
	 * 
	 * If no winner is present for the lottery, then appropriate error message is
	 * returned.
	 * 
	 * 
	 * @param lotteryId - unique identifier of the lottery for which result is
	 *                  requested
	 * 
	 * @return Lottery result with the winner ballot details and prize money will be
	 *         returned. In case of no winner, appropriate error message is returned
	 *
	 * @throws ResourceNotFoundException- exception thrown if no lottery is present
	 *                                    in storage for the request
	 * 
	 * @throws LotteryStatusException     - exception thrown if lottery is not yet
	 *                                    closed and winner is not selected
	 */
	@GetMapping(value = "/lotteryResult")
	public LotteryResultDTO getLotteryResult(@RequestParam(value = "lotteryId", required = true) String lotteryId)
			throws ResourceNotFoundException, LotteryStatusException {

		return lotteryService.getLotteryResult(lotteryId);

	}

	/**
	 * Creates a new lottery in the storage.
	 * 
	 * @param lotteryDto - input request containing the lotteryId, name, prizeMoney
	 *                   and startDate
	 * 
	 * @return success message with created lotteryId
	 * 
	 */
	@PostMapping(value = "/lottery")
	public String createLottery(@RequestBody(required = true) LotteryDTO lotteryDto) {

		return "Lottery created with lotteryId:" + lotteryService.createLottery(lotteryDto);

	}

}
