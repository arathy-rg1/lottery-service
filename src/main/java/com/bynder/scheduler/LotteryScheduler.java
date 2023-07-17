package com.bynder.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bynder.constants.Constants;
import com.bynder.model.Lottery;
import com.bynder.service.BallotService;
import com.bynder.service.LotteryService;
import com.bynder.utils.DateUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Scheduler class to handle scheduled jobs related to lottery processing 
 * 		*end a lottery event at midnight
 * 
 * @author arathy
 *
 */
@Service
@Slf4j
public class LotteryScheduler {

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private BallotService ballotService;

	/**
	 * Scheduler job runs every day at midnight
	 * 
	 * For all the active lotteries, it selects a random winner and close the
	 * lottery
	 * 
	 */
	@Scheduled(cron = "${end.lottery.event}")
	public void endLotteryEvent() {

		List<Lottery> lotteryList = lotteryService.getActiveLotteries();

		if (!CollectionUtils.isEmpty(lotteryList)) {

			lotteryList.forEach(lottery -> {

				//selects a random winner
				String winnerBallot = ballotService.getLotteryWinner(lottery.getLotteryId());

				//closes the lottery
				lotteryService.closeLottery(lottery.getLotteryId(),
						DateUtils.toDate(LocalDateTime.now().toString(), Constants.LOTTERY_DATE_FORMAT), winnerBallot,
						Constants.CLOSED);

				log.info("Winner for {} is {}", lottery.getLotteryId(), winnerBallot);
			});

		} else {

			log.error("No lotteries present to close for today!");
		}

	}

}
