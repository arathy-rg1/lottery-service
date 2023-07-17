package com.bynder.exception.advice;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bynder.exception.APIResponseView;
import com.bynder.exception.EntityExistsException;
import com.bynder.exception.LotteryStatusException;
import com.bynder.exception.MissingMandatoryParamException;
import com.bynder.exception.ResourceNotFoundException;

/**
 * Class to handle exceptions
 * 
 * @author arathy
 *
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {

	/**
	 * Handles Lottery Status Exceptions
	 * 
	 * @param exception - LotteryStatusException
	 * 
	 * @return Returns errorMessage and timestamp with HTTP Status code 400
	 */
	@ExceptionHandler(LotteryStatusException.class)
	public ResponseEntity<Object> handleLotteryStatusException(LotteryStatusException exception) {

		APIResponseView responseView = new APIResponseView(LocalDateTime.now().toString(), exception.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseView);
	}

	/**
	 * Handles Resource Not Found Exceptions
	 * 
	 * @param exception - ResourceNotFoundException
	 * 
	 * @return Returns errorMessage and timestamp with HTTP Status code 404
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception) {

		APIResponseView responseView = new APIResponseView(LocalDateTime.now().toString(), exception.getMessage());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseView);
	}

	/**
	 * Handles Entity Exists Exceptions
	 * 
	 * @param exception - EntityExistsException
	 * 
	 * @return Returns errorMessage and timestamp with HTTP Status code 400
	 */
	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<Object> handleEntityExistsException(EntityExistsException exception) {

		APIResponseView responseView = new APIResponseView(LocalDateTime.now().toString(), exception.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseView);
	}

	/**
	 * Handles Missing Mandatory Request Params exception
	 * 
	 * @param exception - MissingMandatoryParamException
	 * 
	 * @return Returns errorMessage and timestamp with HTTP Status code 400
	 */
	@ExceptionHandler(MissingMandatoryParamException.class)
	public ResponseEntity<Object> handleMissingServletRequestParameterException(
			MissingMandatoryParamException exception) {

		APIResponseView responseView = new APIResponseView(LocalDateTime.now().toString(), exception.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseView);
	}

}
