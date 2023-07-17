package com.bynder.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.bynder.model.Ballot;
import com.bynder.model.Lottery;
import com.bynder.service.impl.BallotServiceImpl;
import com.bynder.service.impl.LotteryServiceImpl;

/**
 * Test class for LotteryScheduler methods
 * 
 * 
 * @author arathy
 *
 */
@SpringBootTest
public class LotterySchedulerTest {

	@Mock
	private BallotServiceImpl ballotService;

	@Mock
	private LotteryServiceImpl lotteryService;

	@InjectMocks
	private LotteryScheduler lotteryScheduler;

	@Before
	public void setupContext() {
		MockitoAnnotations.openMocks(this);
	}

	/**
	 * Tests endLotteryEvent for successful closing of lottery
	 * 
	 */
	@Test
	public void testEndLotteryEvent_Success() {

		Lottery lottery = new Lottery();
		lottery.setLotteryId("12");
		List<Lottery> lotteries = new ArrayList<>();
		lotteries.add(lottery);

		Ballot ballot = new Ballot();
		ballot.setBallotId("11");

		Mockito.when(lotteryService.getActiveLotteries()).thenReturn(lotteries);
		Mockito.when(ballotService.getLotteryWinner(ArgumentMatchers.any())).thenReturn(ballot.getBallotId());
		Mockito.doNothing().when(lotteryService).closeLottery(ArgumentMatchers.any(), ArgumentMatchers.any(),
				ArgumentMatchers.any(), ArgumentMatchers.any());

		lotteryScheduler.endLotteryEvent();

		Mockito.verify(lotteryService).getActiveLotteries();
		Mockito.verify(ballotService).getLotteryWinner("12");

	}

	/**
	 * Tests endLotteryEvent in case of no lottery to be closed
	 * 
	 */
	@Test
	public void testEndLotteryEvent_NoLotteryToClose() {

		Mockito.when(lotteryService.getActiveLotteries()).thenReturn(null);

		lotteryScheduler.endLotteryEvent();
		Mockito.verify(lotteryService).getActiveLotteries();

	}

}
