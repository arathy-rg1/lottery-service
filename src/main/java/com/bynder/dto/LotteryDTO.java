package com.bynder.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class LotteryDTO {

	private String lotteryId;

	private String name;

	private long prizeMoney;

	private String status;

	private String winnerBallot;

	private String startDate;

	private String endDate;

}
