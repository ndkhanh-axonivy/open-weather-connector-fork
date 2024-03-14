package com.axonivy.connector.openweather.util;

import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.commons.lang3.StringUtils;

public class DateTimeFormatterUtilities {

	private DateTimeFormatterUtilities() {
	}

	public static String formatDate(LocalDate date) {
		return date != null ? date.format(Constants.Formatter.DATE) : StringUtils.EMPTY;
	}

	public static String formatTime12Hour(LocalTime time) {
		return time != null ? time.format(Constants.Formatter.TIME_12_HOUR) : StringUtils.EMPTY;
	}

	public static String formatShortDate(LocalDate date) {
		return date != null ? date.format(Constants.Formatter.SHORT_DATE) : StringUtils.EMPTY;
	}

	public static String formatTime24Hour(LocalTime time) {
		return time != null ? time.format(Constants.Formatter.TIME_24_HOUR) : StringUtils.EMPTY;
	}
}
