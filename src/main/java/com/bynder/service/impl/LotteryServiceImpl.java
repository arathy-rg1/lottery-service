package com.bynder.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bynder.constants.Constants;
import com.bynder.dto.LotteryDTO;
import com.bynder.dto.LotteryResultDTO;
import com.bynder.exception.LotteryStatusException;
import com.bynder.exception.ResourceNotFoundException;
import com.bynder.model.Lottery;
import com.bynder.repository.LotteryRepository;
import com.bynder.service.LotteryService;
import com.bynder.service.SequenceGeneratorService;
import com.bynder.utils.DateUtils;

/**
 * Service class to handle the requests related to Lottery operations:
 * 		* get all the lottery present in storage
 * 		* get lottery associated with a lotteryId
 * 		* get the lottery list based on status(open/closed)
 * 		* save a new lottery into the storage
 * 		* get result for a particular lottery if its active
 * 		* get active lotteries
 * 		* close a lottery
 * 
 * @author arathy
 *
 */
@Service
public class LotteryServiceImpl implements LotteryService {

	@Autowired
	private LotteryRepository lotteryRepository;

	@Autowired
	private SequenceGeneratorService sequenceService;

	/**
	 * Returns the list of lotteries present in the storage.
	 * 
	 * Takes an optional parameter status(value can be OPEN/CLOSED) based on which
	 * list of open/closed lottery list is returned
	 * 
	 * If status is not present in request, returns all the lotteries in the storage
	 * 
	 * @param status - optional input parameter(value can be OPEN/CLOSED)
	 * 
	 * @return list of lotteries in the storage based on input request
	 * 
	 * @throws ResourceNotFoundException - exception thrown if no lotteries based on
	 *                                   the request is present in storage
	 */
	public List<LotteryDTO> getLotteries(String status) throws ResourceNotFoundException {

		List<Lottery> lotteryList = null;

		if (StringUtils.isNotBlank(status)) {
			lotteryList = lotteryRepository.findByStatus(status);
		} else {
			lotteryList = lotteryRepository.findAll();
		}

		if (CollectionUtils.isEmpty(lotteryList)) {
			throw new ResourceNotFoundException("Lotteries not found");
		}

		return mapLotteries(lotteryList);

	}

	/**
	 * Maps the list of lotteries returned from storage into List of LotteryDTO
	 * which in turn is passed as response to requester.
	 * 
	 * @param lotteryList - list of lotteries returned from storage
	 * 
	 * @return mapped list of LotteryDTO objects
	 */
	private List<LotteryDTO> mapLotteries(List<Lottery> lotteryList) {

		List<LotteryDTO> lotteryDTOList = new ArrayList<>();

		lotteryList.stream()
				.forEach(lottery -> lotteryDTOList.add(new LotteryDTO(lottery.getLotteryId(), lottery.getName(),
						lottery.getPrizeMoney(), lottery.getStatus(), lottery.getWinnerBallot(),
						DateUtils.formatDate(lottery.getStartDate(), Constants.LOTTERY_DATE_FORMAT),
						DateUtils.formatDate(lottery.getEndDate(), Constants.LOTTERY_DATE_FORMAT))));

		return lotteryDTOList;

	}

	/**
	 * Returns the lottery associated with a lotteryId, if any present in storage.
	 * 
	 * @param lotteryId - unique identifier of lottery
	 * 
	 * @return Lottery associated with the requested id
	 * 
	 */
	public Lottery getLottery(String lotteryId) {

		return lotteryRepository.findByLotteryId(lotteryId);

	}

	/**
	 * Returns the lottery result for a particular lotteryId
	 * 
	 * Only returns the results for closed lotteries
	 * 
	 * If no winner is present for the lottery, then appropriate error message is
	 * returned
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
	public LotteryResultDTO getLotteryResult(String lotteryId)
			throws ResourceNotFoundException, LotteryStatusException {

		Lottery lottery = lotteryRepository.findByLotteryId(lotteryId);

		// checks if lottery is present and is closed
		checkLotteryStatus(lottery);

		// Returns appropriate message in case of no winner for a lottery
		if (StringUtils.equalsIgnoreCase(lottery.getWinnerBallot(), "-1")) {
			return new LotteryResultDTO(null, "Nobody won!",
					DateUtils.formatDate(lottery.getEndDate(), Constants.LOTTERY_DATE_FORMAT), lottery.getPrizeMoney());
		}

		return new LotteryResultDTO(lottery.getWinnerBallot(), null,
				DateUtils.formatDate(lottery.getEndDate(), Constants.LOTTERY_DATE_FORMAT), lottery.getPrizeMoney());

	}

	/**
	 * Checks if lottery is present and is closed. If not, then exception with the
	 * appropriate error message is returned.
	 * 
	 * @param lotteryId - unique identifier of lottery
	 * 
	 * @throws ResourceNotFoundException - exception thrown if the lottery is not
	 *                                   present
	 * 
	 * @throws LotteryStatusException    - exception thrown if lottery is not closed
	 */
	private void checkLotteryStatus(Lottery lottery) throws ResourceNotFoundException, LotteryStatusException {

		if (lottery == null) {
			throw new ResourceNotFoundException("Lottery not found");
		}
		if (StringUtils.equalsIgnoreCase(lottery.getStatus(), Constants.OPEN)) {
			throw new LotteryStatusException("Lottery is not closed yet!");
		}

	}

	/**
	 * Creates a new lottery in the storage.
	 * 
	 * lotteryId is generated from LOTTERY_ID_SEQUENCE
	 * 
	 * @param lotteryDto - input request containing the lotteryId, name, prizeMoney
	 *                   and startDate
	 * 
	 * @return created lotteryId
	 * 
	 */
	public String createLottery(LotteryDTO lotteryDto) {

		Lottery lottery = new Lottery(String.valueOf(sequenceService.getNextSequenceNumber(Constants.LOTTERY_SEQUENCE)),
				lotteryDto.getName(), lotteryDto.getPrizeMoney(),
				DateUtils.toDate(lotteryDto.getStartDate(), Constants.LOTTERY_DATE_FORMAT));

		lottery.setStatus(Constants.OPEN);
		lotteryRepository.save(lottery);

		return lottery.getLotteryId();
	}

	/**
	 * Returns list of active lotteries
	 * 
	 */
	public List<Lottery> getActiveLotteries() {

		return lotteryRepository.findByStatus(Constants.OPEN);

	}

	/**
	 * Closes the lottery and updates the status as CLOSED and end date as current
	 * time
	 * 
	 * @param lotteryId    - unique identifier associated with lottery
	 * @param endDate      - lottery end date
	 * @param winnerBallot - ballotId of winner
	 * @param status       - lottery status to be updated
	 * 
	 */
	public void closeLottery(String lotteryId, Date endDate, String winnerBallot, String status) {

		lotteryRepository.updateLottery(lotteryId, endDate, winnerBallot, status);

	}

}
