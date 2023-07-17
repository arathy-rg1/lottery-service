package com.bynder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bynder.dto.BallotDTO;
import com.bynder.exception.LotteryStatusException;
import com.bynder.exception.ResourceNotFoundException;
import com.bynder.service.BallotService;

/**
 * Controller class to handle the requests related to Ballot operations: 
 * 		* get the ballots for a given userId. 
 * 		* save a new ballot related to a lottery.
 * 
 * @author arathy
 *
 */
@RestController
public class BallotController {

	@Autowired
	private BallotService ballotService;

	/**
	 * Returns the list of ballots.
	 * 
	 * Takes 2 optional parameters userId and lotteryId, based on which
	 * corresponding ballots are returned. If both are not provided, then all the
	 * ballots are returned.
	 * 
	 * If no ballots exists for the particular user, exception with the appropriate
	 * error message is returned.
	 * 
	 * @param userId    - unique identifier related to a customer
	 * @param lotteryId - unique identifier related to a lottery
	 * 
	 * @return list of ballots, if any present
	 * 
	 * @throws ResourceNotFoundException - exception thrown if no ballots are
	 *                                   present
	 */
	@GetMapping(value = "/ballots")
	public List<BallotDTO> getBallots(@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "lotteryId", required = false) String lotteryId) throws ResourceNotFoundException {

		return ballotService.getBallots(userId, lotteryId);

	}

	/**
	 * Creates the ballot associated to a lottery for a particular user.
	 * 
	 * If the given user or lottery does not exists or lottery is not active, then
	 * exception with the appropriate error message is returned.
	 * 
	 * @param ballotDto - ballot dto object request containing userId and lotteryId
	 * 
	 * @return success response with created ballotId
	 * 
	 * @throws ResourceNotFoundException - exception thrown if the user or lottery
	 *                                   is not present
	 * @throws LotteryStatusException    - exception thrown if ballot is submitted
	 *                                   for a closed lottery
	 */
	@PostMapping(value = "/ballot")
	public String createBallot(@RequestBody(required = true) BallotDTO ballotDto)
			throws ResourceNotFoundException, LotteryStatusException {

		String ballotId = ballotService.createBallot(ballotDto);
		return "Ballot created with id:" + ballotId;

	}

}
