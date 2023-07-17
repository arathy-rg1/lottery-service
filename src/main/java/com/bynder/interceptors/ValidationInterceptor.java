package com.bynder.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bynder.constants.Constants;
import com.bynder.exception.MissingMandatoryParamException;

/**
 * Interceptor class handles validation logic for mandatory request parameters
 * 
 * @author arathy
 *
 */
@Component
public class ValidationInterceptor implements HandlerInterceptor {

	/**
	 * Validates incoming requests and check if all the required parameters are
	 * provided in the request.
	 * 
	 * In case of validation error, appropriate exception with error message is
	 * thrown.
	 * 
	 * Validates query parameters coming in request
	 * 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (handler instanceof HandlerMethod) {

			HandlerMethod handlerMethod = (HandlerMethod) handler;

			if (ArrayUtils.isNotEmpty(handlerMethod.getMethod().getParameters())) {

				for (Parameter parameter : handlerMethod.getMethod().getParameters()) {

					Map<String, Object> parameterDetails = retrieveParameterAttributes(parameter, request);
					checkForMandatoryParameter(parameterDetails);
				}

			}
		}

		return true;
	}

	/**
	 * Retrieves the attributes related to the request parameters
	 * 
	 * @param parameter - includes information about method parameters like its name
	 *                  and modifiers
	 * @param request   - current HTTP request
	 * 
	 * @return request parameter attributes
	 */
	private Map<String, Object> retrieveParameterAttributes(Parameter parameter, HttpServletRequest request) {

		Map<String, Object> parameterDetails = new HashMap<>();

		for (Annotation annotation : parameter.getAnnotations()) {

			if (annotation.annotationType().equals(RequestParam.class)) {

				RequestParam param = (RequestParam) annotation;
				parameterDetails.put(Constants.REQUIRED, param.required());
				parameterDetails.put(Constants.NAME, param.value());
				parameterDetails.put(Constants.VALUE, request.getParameter(param.value()));
			}

		}

		return parameterDetails;
	}

	/**
	 * Checks if any mandatory parameter/parameter value is missing
	 * 
	 * @param parameterDetails- request parameter attributes
	 * 
	 * @throws MissingMandatoryParamException - thrown if mandatory
	 *                                        parameter/parameter value is missing
	 */
	private void checkForMandatoryParameter(Map<String, Object> parameterDetails)
			throws MissingMandatoryParamException {

		if (parameterDetails.get(Constants.REQUIRED) != null && (Boolean) parameterDetails.get(Constants.REQUIRED)
				&& isNullOrEmpty(parameterDetails.get(Constants.VALUE))) {

			throw new MissingMandatoryParamException(
					"Mandatory Parameter Missing. Please provide value for " + parameterDetails.get(Constants.NAME));
		}

	}

	/**
	 * Check if an object is null
	 * 
	 * For string objects, checks whether it is null, empty or contains whitespace
	 * 
	 * For collection objects, checks whether if the supplied collection is null or
	 * empty
	 * 
	 * @param paramValue - parameter for which null/empty check needs to be
	 *                   performed
	 * 
	 * @return true/false depending on the checked conditions
	 */
	private boolean isNullOrEmpty(Object parameter) {

		if (parameter == null) {
			return true;
		}

		if (parameter instanceof String) {
			return StringUtils.isBlank((String) parameter);
		}

		return false;
	}

}
