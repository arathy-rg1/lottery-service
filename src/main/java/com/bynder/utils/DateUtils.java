package com.bynder.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

/**
 * Util class related to date operations 
 * 		* converts date object to a string of particular date format 
 * 		* converts string date to a date object of particular format
 * 
 * @author arathy
 *
 */
@Slf4j
public class DateUtils {

	/**
	 * private constructor to hide implicit public constructor
	 */
	private DateUtils() {

	}

	/**
	 * Converts a date object to a string of particular date format
	 * 
	 * @param date       - date to be converted
	 * @param dateFormat - format to which date needs to be converted
	 * 
	 * @return formatted date string
	 */
	public static String formatDate(Date date, String dateFormat) {

		String formattedDate = null;
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			formattedDate = formatter.format(date);
		}
		return formattedDate;
	}

	/**
	 * Converts a string date to a Date object of particular format
	 * 
	 * @param date       - date to be converted
	 * @param dateFormat - format to which date needs to be converted
	 * 
	 * @return formatted date string
	 */
	public static Date toDate(String date, String dateFormat) {

		Date formattedDate = null;
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			try {
				formattedDate = formatter.parse(date);
			} catch (ParseException exception) {
				log.error("Error while parsing date {} using format {}", date, dateFormat);
			}
		}
		return formattedDate;
	}

}
