package com.bynder.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.bynder.model.User;
import com.bynder.repository.UserRepository;
import com.bynder.service.SequenceGeneratorService;
import com.bynder.service.impl.UserServiceImpl;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * Test class for UserController methods
 * 
 * Runs as a parameterized test class
 * 
 * @author arathy
 *
 */
@RunWith(JUnitParamsRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserRepository userRepository;

	@Mock
	private SequenceGeneratorService sequenceService;

	@InjectMocks
	private UserServiceImpl userService;

	private static final String SUCCESS = "SUCCESS";
	private static final String USER_EXISTS = "USER_EXISTS";

	@Before
	public void setUpContext() throws Exception {

		new TestContextManager(getClass()).prepareTestInstance(this);
	}

	/**
	 * Tests registerUser API
	 * 
	 * scenarios: successful registration, username already exists
	 * 
	 * @param scenario           - scenario to be tested
	 * @param expectedStatusCode - expected HTTP status code of API
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@Test
	@Parameters(method = "testRegisterUser_Params")
	public void testRegisterUser(String scenario, int expectedStatusCode) throws Throwable {

		String requestBody = "{\"userName\": \"jsmith\",\"firstName\": \"John\",\"lastName\": \"Smith\"}";
		switch (scenario) {

		case SUCCESS:

			Mockito.when(userRepository.findByUserName(ArgumentMatchers.any())).thenReturn(null);
			Mockito.when(sequenceService.getNextSequenceNumber(ArgumentMatchers.any())).thenReturn(1L);
			Mockito.when(userRepository.save(ArgumentMatchers.any())).thenReturn(null);

			break;

		case USER_EXISTS:

			Mockito.when(userRepository.findByUserName(ArgumentMatchers.any())).thenReturn(new User());
			break;

		}

		ResultActions actions = mockMvc
				.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(requestBody));

		actions.andExpect(status().is(expectedStatusCode));
	}

	/**
	 * Parameters related to different scenarios for testing registerUser API
	 * 
	 * @return scenario parameters
	 * 
	 * @throws Throwable - thrown if any exception occurs
	 */
	@SuppressWarnings("unused")
	private static Object[][] testRegisterUser_Params() throws Throwable {

		return new Object[][] {

				{ SUCCESS, 200 }, { USER_EXISTS, 400 } };
	}

}
