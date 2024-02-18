package com.axonivy.connector.openweather.dto;

import com.axonivy.connector.openweather.util.DateTimeFormatterUtilities;

public class DailyForecastDisplayInfo {

	private final DailyForecast dailyForecast;
	private final int chartWindowStartX;
	private final int chartWindowEndX;
	private final String formattedEEE;

	public DailyForecastDisplayInfo(DailyForecast dailyForecast, int chartWindowStartX, int chartWindowEndX) {
		this.dailyForecast = dailyForecast;
		this.chartWindowStartX = chartWindowStartX;
		this.chartWindowEndX = chartWindowEndX;
		this.formattedEEE = dailyForecast != null ? DateTimeFormatterUtilities.formatEEE(dailyForecast.getDate()) : null;
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
	
	public String getFormattedEEE() {
		return formattedEEE;
	}
}
