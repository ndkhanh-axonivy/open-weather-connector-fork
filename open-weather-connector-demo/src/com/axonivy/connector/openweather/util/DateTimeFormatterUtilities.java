package com.axonivy.connector.openweather.util;

import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.commons.lang3.StringUtils;

public class DateTimeFormatterUtilities {

	private DateTimeFormatterUtilities() {
	}

	public static String formatDate(LocalDate date) {
		return date != null ? date.format(Constants.DATE_FORMATTER) : StringUtils.EMPTY;
	}

	public static String formatTime12Hour(LocalTime time) {
		return time != null ? time.format(Constants.TIME_12_HOUR_FORMATTER) : StringUtils.EMPTY;
	}

	public static String formatEEE(LocalDate date) {
		return date != null ? date.format(Constants.EEE_FORMATTER) : StringUtils.EMPTY;
	}

	public static String formatTime24Hour(LocalTime time) {
		return time != null ? time.format(Constants.TIME_24_HOUR_FORMATTER) : StringUtils.EMPTY;
	}
}
