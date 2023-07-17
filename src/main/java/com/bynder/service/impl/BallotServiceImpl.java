package com.bynder.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bynder.constants.Constants;
import com.bynder.dto.BallotDTO;
import com.bynder.exception.LotteryStatusException;
import com.bynder.exception.ResourceNotFoundException;
import com.bynder.model.Ballot;
import com.bynder.model.Lottery;
import com.bynder.repository.BallotRepository;
import com.bynder.service.BallotService;
import com.bynder.service.LotteryService;
import com.bynder.service.SequenceGeneratorService;
import com.bynder.service.UserService;
import com.bynder.utils.DateUtils;

/**
 * Service class to handle the requests related to Ballot operations:
 * 		* get the ballots for a given userId.
 * 		* save a new ballot related to a lottery.
 * 		* selects winner ballot for a lottery.
 * 
 * @author arathy
 *
 */
@Service
public class BallotServiceImpl implements BallotService {

	@Autowired
	private BallotRepository ballotRepository;

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private UserService userService;

	@Autowired
	private SequenceGeneratorService sequenceService;

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
	public List<BallotDTO> getBallots(String userId, String lotteryId) throws ResourceNotFoundException {

		List<Ballot> ballotList = null;

		if (StringUtils.isNoneBlank(userId, lotteryId)) {

			ballotList = ballotRepository.findByUserIdAndLotteryId(userId, lotteryId);

		} else if (StringUtils.isNotBlank(userId)) {

			ballotList = ballotRepository.findByUserId(userId);

		} else if (StringUtils.isNotBlank(lotteryId)) {
			
			ballotList = ballotRepository.findByLotteryId(lotteryId);
			
		} else {
			
			ballotList = ballotRepository.findAll();
		}

		if (CollectionUtils.isEmpty(ballotList)) {
			throw new ResourceNotFoundException("Ballots Not Found");
		}

		return mapBallotDTO(ballotList);

	}

	/**
	 * Maps the list of ballots returned from storage into List of BallotDTO which
	 * in turn is passed as response to requester.
	 * 
	 * @param ballotList - list of ballots returned from storage
	 * 
	 * @return mapped list of BallotDTO objects
	 */
	private List<BallotDTO> mapBallotDTO(List<Ballot> ballotList) {

		List<BallotDTO> ballotDTOList = new ArrayList<>();

		ballotList.stream().forEach(ballot -> ballotDTOList.add(new BallotDTO(ballot.getLotteryId(), ballot.getUserId(),
				ballot.getBallotId(), DateUtils.formatDate(ballot.getCreatedDate(), Constants.LOTTERY_DATE_FORMAT))));

		return ballotDTOList;
	}

	/**
	 * Stores the ballot associated to a lottery for a particular user.
	 * 
	 * If the given user or lottery does not exists or lottery is not active, then
	 * exception with the appropriate error message is returned.
	 * 
	 * 
	 * @param ballotDto - ballot dto object request containing userId and lotteryId
	 * 
	 * @return created ballotId
	 * 
	 * @throws ResourceNotFoundException - exception thrown if the user or lottery
	 *                                   is not present
	 * @throws LotteryStatusException    - exception thrown if ballot is submitted
	 *                                   for a closed lottery
	 */
	public String createBallot(BallotDTO ballotDto) throws ResourceNotFoundException, LotteryStatusException {

		// checks if given user is present in storage
		checkIsUserPresent(ballotDto.getUserId());

		// checks if lottery is still active
		checkLotteryStatus(ballotDto.getLotteryId());

		Ballot ballot = mapBallotEntity(ballotDto);
		ballotRepository.save(ballot);

		return String.valueOf(ballot.getBallotId());
	}

	/**
	 * Checks if user is present in storage.
	 * 
	 * If not present, then exception with the appropriate error message is
	 * returned.
	 * 
	 * @param userId - unique identifier of user
	 * 
	 * @throws ResourceNotFoundException- exception thrown if the user is not
	 *                                    present
	 */
	private void checkIsUserPresent(String userId) throws ResourceNotFoundException {

		if (StringUtils.isBlank(userId) || userService.findUser(userId) == null) {

			throw new ResourceNotFoundException("User Not found");
		}
	}

	/**
	 * Checks if lottery is present and is active. If not, then exception with the
	 * appropriate error message is returned.
	 * 
	 * @param lotteryId - unique identifier of lottery
	 * 
	 * @throws ResourceNotFoundException - exception thrown if the lottery is not
	 *                                   present
	 * 
	 * @throws LotteryStatusException    - exception thrown if lottery is not active
	 */
	private void checkLotteryStatus(String lotteryId) throws ResourceNotFoundException, LotteryStatusException {

		Lottery lottery = lotteryService.getLottery(lotteryId);

		if (lottery == null) {
			throw new ResourceNotFoundException("Lottery Not found");

		} else if (StringUtils.equalsIgnoreCase(lottery.getStatus(), Constants.CLOSED)) {

			throw new LotteryStatusException("Lottery is Closed");
		}
	}

	/**
	 * Maps the BallotDTO object into Ballot entity.
	 * 
	 * BallotId is generated from BALLOT_ID_SEQUENCE
	 * 
	 * @param ballotDto - input BallotDTO request
	 * 
	 * @return mapped Ballot entity
	 */
	private Ballot mapBallotEntity(BallotDTO ballotDto) {

		Ballot ballot = new Ballot();
		ballot.setUserId(ballotDto.getUserId());
		ballot.setLotteryId(ballotDto.getLotteryId());
		ballot.setCreatedDate(DateUtils.toDate(LocalDateTime.now().toString(), Constants.LOTTERY_DATE_FORMAT));
		ballot.setBallotId(String.valueOf(sequenceService.getNextSequenceNumber(Constants.BALLOT_SEQUENCE)));

		return ballot;
	}

	/**
	 * Selects a random ballot as winner for a lottery.
	 * 
	 * If no ballots are associated with a lottery, default -1 is considered as
	 * winner ballot.
	 * 
	 * @param lotteryId - identifier of lottery for which winner needs to be
	 *                  determined
	 * 
	 * @return ballotId for the randomly selected winner
	 * 
	 */
	public String getLotteryWinner(String lotteryId) {

		// counts the number of ballots associated with lottery
		Long ballotCount = ballotRepository.countByLotteryId(lotteryId);

		// -1 is returned if no ballots are associated with lottery
		if (ballotCount == 0) {
			return "-1";
		}

		// finds a random ballot as winner
		Ballot winnerBallot = ballotRepository.findRandomBallot(lotteryId);
		return winnerBallot.getBallotId();
	}

}
