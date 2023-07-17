package com.bynder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.bynder.model.Ballot;
import com.bynder.repository.BallotRepository;
import com.bynder.service.impl.BallotServiceImpl;

/**
 * Test class for BallotService methods
 * 
 * 
 * @author arathy
 *
 */
@SpringBootTest
public class BallotServiceTest {

	@Mock
	private BallotRepository ballotRepository;

	@InjectMocks
	private BallotServiceImpl ballotService;

	@Before
	public void setupContext() {
		MockitoAnnotations.openMocks(this);
	}

	/**
	 * Tests getLotteryWinner for successful random winner selection for a lottery
	 * 
	 */
	@Test
	public void testGetLotteryWinner() {

		Ballot ballot = new Ballot();
		ballot.setBallotId("11");

		Mockito.when(ballotRepository.countByLotteryId(ArgumentMatchers.any())).thenReturn(3L);
		Mockito.when(ballotRepository.findRandomBallot(ArgumentMatchers.any())).thenReturn(ballot);

		String response = ballotService.getLotteryWinner("12");
		assertEquals("11", response);

	}

	/**
	 * Tests getLotteryWinner for no winner selected due to unavailability of
	 * ballots for a lottery
	 * 
	 */
	@Test
	public void testGetLotteryWinner_NoWinner() {

		Mockito.when(ballotRepository.countByLotteryId(ArgumentMatchers.any())).thenReturn(0L);

		String response = ballotService.getLotteryWinner("12");
		assertEquals("-1", response);

	}

}
