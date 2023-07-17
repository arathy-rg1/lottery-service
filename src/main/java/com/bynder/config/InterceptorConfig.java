package com.bynder.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bynder.interceptors.ValidationInterceptor;

/**
 * Configuration class for injecting customized interceptor class
 * 
 * @author arathy
 *
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

	@Autowired
	private ValidationInterceptor validationInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(validationInterceptor);
	}

}
