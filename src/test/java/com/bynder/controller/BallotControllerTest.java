package com.bynder.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import com.bynder.model.Ballot;
import com.bynder.model.Lottery;
import com.bynder.model.User;
import com.bynder.repository.BallotRepository;
import com.bynder.repository.LotteryRepository;
import com.bynder.repository.UserRepository;
import com.bynder.service.SequenceGeneratorService;
import com.bynder.service.impl.BallotServiceImpl;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * Test class for BallotController methods
 * 
 * Runs as a parameterized test class
 * 
 * @author arathy
 *
 */
@RunWith(JUnitParamsRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BallotControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LotteryRepository lotteryRepository;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private BallotRepository ballotRepository;

	@Mock
	private SequenceGeneratorService sequenceService;

	@InjectMocks
	private BallotServiceImpl ballotService;

	private static final String SUCCESS = "SUCCESS";
	private static final String SUCCESS_USERID = "SUCCESS_USERID";
	private static final String SUCCESS_LOTTERYID = "SUCCESS_LOTTERYID";
	private static final String SUCCESS_USERID_LOTTERYID = "SUCCESS_USERID_LOTTERYID";
	private static final String SUCCESS_ALL = "SUCCESS_ALL";
	private static final String NO_BALLOTS = "NO_BALLOTS";
	private static final String LOTTERY_CLOSED = "LOTTERY_CLOSED";
	private static final String USER_NOT_PRESENT = "USER_NOT_PRESENT";
	private static final String LOTTERY_NOT_PRESENT = "LOTTERY_NOT_PRESENT";

	@Before
	public void setUpContext() throws Exception {

		new TestContextManager(getClass()).prepareTestInstance(this);
	}

	/**
	 * Tests getBallot API
	 * 
	 * scenarios: successful response with ballot list and not found error in case
	 * ballots are not present in storage
	 * 
	 * @param scenario           - scenario to be tested
	 * @param userId             - unique identifier related to a customer
	 * @param lotteryId          - unique identifier related to a lottery
	 * @param expectedStatusCode - expected HTTP status code of API
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@Test
	@Parameters(method = "testGetBallots_Params")
	public void testGetBallots(String scenario, String userId, String lotteryId, int expectedStatusCode)
			throws Throwable {

		List<Ballot> ballots = new ArrayList<>();
		ballots.add(new Ballot());

		switch (scenario) {

		case SUCCESS_USERID:

			Mockito.when(ballotRepository.findByUserId(ArgumentMatchers.any())).thenReturn(ballots);

			break;

		case SUCCESS_LOTTERYID:

			Mockito.when(ballotRepository.findByLotteryId(ArgumentMatchers.any())).thenReturn(ballots);

			break;

		case SUCCESS_USERID_LOTTERYID:

			Mockito.when(ballotRepository.findByUserIdAndLotteryId(ArgumentMatchers.any(), ArgumentMatchers.any()))
					.thenReturn(ballots);

			break;

		case SUCCESS_ALL:

			Mockito.when(ballotRepository.findAll()).thenReturn(ballots);

			break;

		case NO_BALLOTS:

			Mockito.when(ballotRepository.findAll()).thenReturn(null);
			break;

		}

		ResultActions actions = mockMvc.perform(get("/ballots").param("userId", userId).param("lotteryId", lotteryId));

		actions.andExpect(status().is(expectedStatusCode));
	}

	/**
	 * Parameters related to different scenarios for testing getBallot API
	 * 
	 * @return scenario parameters
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@SuppressWarnings("unused")
	private static Object[][] testGetBallots_Params() throws Throwable {

		return new Object[][] {

				{ SUCCESS_USERID, "12", "", 200 }, { SUCCESS_LOTTERYID, "", "11", 200 },
				{ SUCCESS_USERID_LOTTERYID, "12", "11", 200 }, { SUCCESS_ALL, null, null, 200 },
				{ NO_BALLOTS, " ", " ", 404 } };
	}

	/**
	 * Tests saveBallot API
	 * 
	 * scenarios: successful save, user or lottery not present in storage, lottery
	 * is closed
	 * 
	 * @param scenario           - scenario to be tested
	 * @param expectedStatusCode - expected HTTP status code of API
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@Test
	@Parameters(method = "testSaveBallots_Params")
	public void testSaveBallots(String scenario, int expectedStatusCode) throws Throwable {

		String requestBody = "{\"lotteryId\": \"123\",\"userId\": \"3\"}";
		Lottery lottery = null;

		switch (scenario) {

		case SUCCESS:

			Mockito.when(userRepository.findByUserId(ArgumentMatchers.any())).thenReturn(new User());

			lottery = new Lottery();
			lottery.setStatus(Constants.OPEN);
			Mockito.when(lotteryRepository.findByLotteryId(ArgumentMatchers.any())).thenReturn(lottery);

			Mockito.when(sequenceService.getNextSequenceNumber(ArgumentMatchers.any())).thenReturn(1L);

			Mockito.when(ballotRepository.save(ArgumentMatchers.any())).thenReturn(null);

			break;

		case USER_NOT_PRESENT:

			Mockito.when(userRepository.findByUserId(ArgumentMatchers.any())).thenReturn(null);
			break;

		case LOTTERY_NOT_PRESENT:

			Mockito.when(userRepository.findByUserId(ArgumentMatchers.any())).thenReturn(new User());

			Mockito.when(lotteryRepository.findByLotteryId(ArgumentMatchers.any())).thenReturn(null);
			break;

		case LOTTERY_CLOSED:

			Mockito.when(userRepository.findByUserId(ArgumentMatchers.any())).thenReturn(new User());

			lottery = new Lottery();
			lottery.setStatus(Constants.CLOSED);
			Mockito.when(lotteryRepository.findByLotteryId(ArgumentMatchers.any())).thenReturn(lottery);
			break;

		}

		ResultActions actions = mockMvc
				.perform(post("/ballot").contentType(MediaType.APPLICATION_JSON).content(requestBody));

		actions.andExpect(status().is(expectedStatusCode));
	}

	/**
	 * Parameters related to different scenarios for testing saveBallot API
	 * 
	 * @return scenario parameters
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@SuppressWarnings("unused")
	private static Object[][] testSaveBallots_Params() throws Throwable {

		return new Object[][] {

				{ SUCCESS, 200 }, { USER_NOT_PRESENT, 404 }, { LOTTERY_NOT_PRESENT, 404 }, { LOTTERY_CLOSED, 400 } };
	}

}
