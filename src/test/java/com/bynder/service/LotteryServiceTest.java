package com.bynder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.bynder.model.Lottery;
import com.bynder.repository.LotteryRepository;
import com.bynder.service.impl.LotteryServiceImpl;

/**
 * Test class for LotteryService methods
 * 
 * 
 * @author arathy
 *
 */
@SpringBootTest
public class LotteryServiceTest {

	@Mock
	private LotteryRepository lotteryRepository;

	@InjectMocks
	private LotteryServiceImpl lotteryService;

	@Before
	public void setupContext() {
		MockitoAnnotations.openMocks(this);
	}

	/**
	 * Tests getting all active lotteries
	 * 
	 */
	@Test
	public void testActiveLotteries() {

		Lottery lottery = new Lottery();
		lottery.setLotteryId("12");
		lottery.setStatus("OPEN");
		List<Lottery> lotteries = new ArrayList<>();
		lotteries.add(lottery);

		Mockito.when(lotteryRepository.findByStatus(ArgumentMatchers.any())).thenReturn(lotteries);

		assertEquals(lotteries, lotteryService.getActiveLotteries());

	}

	/**
	 * Tests closing a lottery
	 * 
	 */
	@Test
	public void testCloseLottery() {

		Date date = new Date();
		Mockito.doNothing().when(lotteryRepository).updateLottery(ArgumentMatchers.any(), ArgumentMatchers.any(),
				ArgumentMatchers.any(), ArgumentMatchers.any());

		lotteryService.closeLottery("12", date, "11", "CLOSED");
		Mockito.verify(lotteryRepository).updateLottery("12", date, "11", "CLOSED");

	}

}
