package com.axonivy.connector.openweather.dto;

import com.axonivy.connector.openweather.util.DateTimeFormatterUtilities;

public class DailyForecastDisplayInfo {

	private final DailyForecast dailyForecast;
	private final int chartWindowStartX;
	private final int chartWindowEndX;
	private final String shortDateName;

	public DailyForecastDisplayInfo(DailyForecast dailyForecast, int chartWindowStartX, int chartWindowEndX) {
		this.dailyForecast = dailyForecast;
		this.chartWindowStartX = chartWindowStartX;
		this.chartWindowEndX = chartWindowEndX;
		this.shortDateName = dailyForecast != null ? DateTimeFormatterUtilities.formatShortDate(dailyForecast.getDate()) : null;
	}

	public DailyForecast getDailyForecast() {
		return dailyForecast;
	}

	public int getChartWindowStartX() {
		return chartWindowStartX;
	}
	
	public int getChartWindowEndX() {
		return chartWindowEndX;
	}
	
	public String getShortDateName() {
		return shortDateName;
	}
}
