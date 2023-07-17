package com.bynder.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.bynder.constants.Constants;
import com.bynder.model.Lottery;
import com.bynder.repository.LotteryRepository;
import com.bynder.service.SequenceGeneratorService;
import com.bynder.service.impl.LotteryServiceImpl;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * Test class for LotteryController methods
 * 
 * Runs as a parameterized test class
 * 
 * @author arathy
 *
 */
@RunWith(JUnitParamsRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LotteryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LotteryRepository lotteryRepository;

	@Mock
	private SequenceGeneratorService sequenceService;

	@InjectMocks
	private LotteryServiceImpl lotteryService;

	private static final String SUCCESS_ALL_LOTTERIES = "SUCCESS_ALL_LOTTERIES";
	private static final String SUCCESS_OPEN_LOTTERIES = "SUCCESS_OPEN_LOTTERIES";
	private static final String NO_LOTTERY = "NO_LOTTERY";

	private static final String SUCCESS = "SUCCESS";
	private static final String INVALID_REQUEST = "INVALID_REQUEST";
	private static final String SUCCESS_NO_WINNER = "SUCCESS_NO_WINNER";
	private static final String OPEN_LOTTERY = "OPEN_LOTTERY";

	@Before
	public void setUpContext() throws Exception {

		new TestContextManager(getClass()).prepareTestInstance(this);
	}

	/**
	 * Tests getLotteries API
	 * 
	 * scenarios: get all/open/closed lotteries
	 * 
	 * @param scenario           - scenario to be tested
	 * @param status             - lottery status
	 * @param expectedStatusCode - expected HTTP status code of API
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@Test
	@Parameters(method = "testGetLotteries_Params")
	public void testGetLotteries(String scenario, String status, int expectedStatusCode) throws Throwable {

		List<Lottery> lotteries = new ArrayList<>();
		lotteries.add(new Lottery());

		switch (scenario) {

		case SUCCESS_ALL_LOTTERIES:

			Mockito.when(lotteryRepository.findAll()).thenReturn(lotteries);

			break;

		case SUCCESS_OPEN_LOTTERIES:

			Mockito.when(lotteryRepository.findByStatus(ArgumentMatchers.any())).thenReturn(lotteries);
			break;

		case NO_LOTTERY:

			lotteries = null;
			Mockito.when(lotteryRepository.findAll()).thenReturn(null);
			break;

		}

		ResultActions actions = mockMvc.perform(get("/lotteries").param("status", status));

		actions.andExpect(status().is(expectedStatusCode));
	}

	/**
	 * Parameters related to different scenarios for testing getLotteries API
	 * 
	 * @return scenario parameters
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@SuppressWarnings("unused")
	private static Object[][] testGetLotteries_Params() throws Throwable {

		return new Object[][] {

				{ SUCCESS_ALL_LOTTERIES, null, 200 }, { SUCCESS_OPEN_LOTTERIES, "OPEN", 200 },
				{ NO_LOTTERY, null, 404 } };
	}

	/**
	 * Tests getLotteryResult API
	 * 
	 * scenarios: get lottery result with/without winner, lottery not present,
	 * lottery is open
	 * 
	 * @param scenario           - scenario to be tested
	 * @param lotteryId          - unique identifier related to a lottery
	 * @param expectedStatusCode - expected HTTP status code of API
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@Test
	@Parameters(method = "testGetLotteryResult_Params")
	public void testGetLotteryResult(String scenario, String lotteryId, int expectedStatusCode) throws Throwable {

		Lottery lottery = new Lottery();

		switch (scenario) {

		case SUCCESS:
			lottery.setStatus(Constants.CLOSED);
			lottery.setWinnerBallot("11");
			lottery.setEndDate(new Date());

			Mockito.when(lotteryRepository.findByLotteryId(ArgumentMatchers.any())).thenReturn(lottery);

			break;

		case SUCCESS_NO_WINNER:

			lottery.setStatus(Constants.CLOSED);
			lottery.setWinnerBallot("-1");

			Mockito.when(lotteryRepository.findByLotteryId(ArgumentMatchers.any())).thenReturn(lottery);
			break;

		case NO_LOTTERY:

			Mockito.when(lotteryRepository.findByLotteryId(ArgumentMatchers.any())).thenReturn(null);
			break;

		case OPEN_LOTTERY:

			lottery.setStatus(Constants.OPEN);
			Mockito.when(lotteryRepository.findByLotteryId(ArgumentMatchers.any())).thenReturn(lottery);
			break;

		}

		ResultActions actions = mockMvc.perform(get("/lotteryResult").param("lotteryId", lotteryId));

		actions.andExpect(status().is(expectedStatusCode));
	}

	/**
	 * Parameters related to different scenarios for testing getLotteryResult API
	 * 
	 * @return scenario parameters
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@SuppressWarnings("unused")
	private static Object[][] testGetLotteryResult_Params() throws Throwable {

		return new Object[][] {

				{ SUCCESS, "12", 200 }, { SUCCESS_NO_WINNER, "12", 200 }, { INVALID_REQUEST, null, 400 },
				{ NO_LOTTERY, "12", 404 }, { OPEN_LOTTERY, "12", 400 } };
	}

	/**
	 * Tests createLottery API
	 * 
	 * scenarios: successful creation
	 * 
	 * @param scenario           - scenario to be tested
	 * @param expectedStatusCode - expected HTTP status code of API
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@Test
	@Parameters(method = "testCreateLottery_Params")
	public void testCreateLottery(String scenario, int expectedStatusCode) throws Throwable {

		String requestBody = "{\"lotteryId\": \"125\",\"name\": \"Lottery B\",\"prizeMoney\": 3601,\"startDate\": \"2023-07-10T13:30:00\"}";

		switch (scenario) {

		case SUCCESS:

			Mockito.when(sequenceService.getNextSequenceNumber(ArgumentMatchers.any())).thenReturn(1L);
			Mockito.when(lotteryRepository.save(ArgumentMatchers.any())).thenReturn(null);

			break;

		}

		ResultActions actions = mockMvc
				.perform(post("/lottery").contentType(MediaType.APPLICATION_JSON).content(requestBody));

		actions.andExpect(status().is(expectedStatusCode));
	}

	/**
	 * Parameters related to different scenarios for testing createLottery API
	 * 
	 * @return scenario parameters
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@SuppressWarnings("unused")
	private static Object[][] testCreateLottery_Params() throws Throwable {

		return new Object[][] {

				{ SUCCESS, 200 } };
	}

}
