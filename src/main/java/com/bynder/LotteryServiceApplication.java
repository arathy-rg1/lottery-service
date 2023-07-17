package com.bynder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LotteryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LotteryServiceApplication.class, args);
	}

}
