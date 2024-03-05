package com.axonivy.connector.openweather.util;

import java.time.format.DateTimeFormatter;

public class Constants {

	public static final String CELSIUS_TYPE_OF_DEGREE = "CELSIUS";
	public static final String FAHRENHEIT_TYPE_OF_DEGREE = "FAHRENHEIT";
	public static final String IMPERIAL_UNITS = "imperial";
	public static final String METRIC_UNITS = "metric";
	public static final String SPEED_METER_UNIT = "m/s";
	public static final String SPEED_MILE_UNIT = "mph";

	public static final String DEFAULT_WEATHER_CONDITION_ICON = "01d";
	public static final String DEFAULT_WEATHER_CONDITION_DESCRIPTION = "clear sky";
	public static final int DEFAULT_WEATHER_TEMPERATURE_DEGREE = -999;
	public static final int DEFAULT_WEATHER_HUMIDITY = 0;
	public static final float DEFAULT_WEATHER_WIND_SPEED = 0.0f;

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("E, d MMMM yyyy");
	public static final DateTimeFormatter TIME_12_HOUR_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
	public static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE");
	public static final DateTimeFormatter TIME_24_HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	public static final String DEFAULT_SEARCH_CITY_NAME = "New York";
	public static final String DEFAULT_UNITS = METRIC_UNITS;
	public static final String DEFAULT_TYPE_OF_DEGREE = CELSIUS_TYPE_OF_DEGREE;
	public static final String DEFAULT_SPEED_UNIT = SPEED_METER_UNIT;
	public static final int DEFAULT_CHART_WINDOW_SIZE = 8;
	
	public static final String FORECAST_WEATHER_CONNECTOR = "connector/ForecastWeather";
	public static final String GET_FORECAST_WEATHER_BY_LOCATION_START_NAME = "getForecastWeatherByLocationName";
	public static final String CITY_NAME_PARAM_NAME = "cityName";
	public static final String COUNTRY_CODE_PARAM_NAME = "countryCode";
	public static final String STATE_CODE_PARAM_NAME = "stateCode";
	public static final String UNITS_PARAM_NAME = "units";
	public static final String FORECAST_WEATHER_RESULT_NAME = "forecastWeather";

	private Constants() {
	}
}
