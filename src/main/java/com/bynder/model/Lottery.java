package com.bynder.model;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "LOTTERY")
@Getter
@Setter
@NoArgsConstructor
public class Lottery {

	@Indexed(unique = true)
	private String lotteryId;

	private String name;

	private long prizeMoney;

	private String status;

	private String winnerBallot;

	private Date startDate;

	private Date endDate;

	public Lottery(String lotteryId, String name, long prizeMoney, Date startDate) {

		this.lotteryId = lotteryId;
		this.name = name;
		this.prizeMoney = prizeMoney;
		this.startDate = startDate;
	}

}
