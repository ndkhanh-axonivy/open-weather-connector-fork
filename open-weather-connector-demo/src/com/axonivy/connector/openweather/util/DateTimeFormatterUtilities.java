package com.axonivy.connector.openweather.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

public class DateTimeFormatterUtilities {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("E, d MMMM yyyy");
	private static final DateTimeFormatter TIME_12_HOUR_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
	private static final DateTimeFormatter EEE_FORMATTER = DateTimeFormatter.ofPattern("EEE");

	public static String formatDate(LocalDate date) {
		return date != null ? date.format(DATE_FORMATTER) : StringUtils.EMPTY;
	}

	public static String formatTime12Hour(LocalTime time) {
		return time != null ? time.format(TIME_12_HOUR_FORMATTER) : StringUtils.EMPTY;
	}
	
	public static String formatEEE(LocalDate date) {
		return date != null ? date.format(EEE_FORMATTER) : StringUtils.EMPTY;
	}
}
