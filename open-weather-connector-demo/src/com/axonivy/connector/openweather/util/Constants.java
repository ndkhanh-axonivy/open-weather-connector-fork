package com.axonivy.connector.openweather.util;

import java.time.format.DateTimeFormatter;

public interface Constants {

	public interface UiDisplay {
		public static final String SPEED_METER_UNIT = "m/s";
		public static final String SPEED_MILE_UNIT = "mph";
	}

	public interface UiVariable {
		public static final String CITY_NAME_PARAM_NAME = "cityName";
		public static final String COUNTRY_CODE_PARAM_NAME = "countryCode";
		public static final String STATE_CODE_PARAM_NAME = "stateCode";
		public static final String UNITS_PARAM_NAME = "units";
		public static final String FORECAST_WEATHER_RESULT_NAME = "forecastWeather";
		public static final String FORECAST_WEATHER_CONNECTOR = "connector/ForecastWeather";
		public static final String GET_FORECAST_WEATHER_BY_LOCATION_START_NAME = "getForecastWeatherByLocationName";
		public static final String TEMPERATURE_CHART_EXTENDER_JS_METHOD_NAME = "temperatureChartExtender";
		public static final String PRECIPITATION_CHART_EXTENDER_JS_METHOD_NAME = "precipitationChartExtender";
		public static final String WIND_CHART_EXTENDER_JS_METHOD_NAME = "windChartExtender";
		public static final String SELECTED_TIME_INDEX_JS_VARIABLE_NAME = "selectedTimeIndex";
	}

	public interface Formatter {
		public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("E, d MMMM yyyy");
		public static final DateTimeFormatter TIME_12_HOUR = DateTimeFormatter.ofPattern("h:mm a");
		public static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("EEE");
		public static final DateTimeFormatter TIME_24_HOUR = DateTimeFormatter.ofPattern("HH:mm");
	}

	public interface Default {
		public static final String SEARCHED_CITY_VARIABLE_PATH = "openWeatherConnectorDemo_defaultSearchedCity";
		public static final String UNITS_VARIABLE_PATH = "openWeatherConnectorDemo_defaultUnits";
		public static final String TYPE_OF_DEGREE = OpenWeatherMapVariable.CELSIUS_TYPE_OF_DEGREE;
		public static final String SPEED_UNIT = UiDisplay.SPEED_METER_UNIT;
		public static final int CHART_WINDOW_SIZE = 8;
		public static final String WEATHER_CONDITION_ICON = "01d";
		public static final String WEATHER_CONDITION_DESCRIPTION = "clear sky";
		public static final int WEATHER_TEMPERATURE_DEGREE = -999;
		public static final int WEATHER_HUMIDITY = 0;
		public static final float WEATHER_WIND_SPEED = 0.0f;
		public static final int WEATHER_WIND_DEGREE = 0;
	}

	public interface Chart {
		public static final String TEMPERATURE_DATASET_LABEL = "temperature";
		public static final String PRECIPITATION_DATASET_LABEL = "precipitation";
		public static final String WIND_DATASET_LABEL = "wind";
	}

	public interface OpenWeatherMapVariable {
		public static final Character NOTATION_DAY = 'd';
		public static final Character NOTATION_NIGHT = 'n';
		public static final String CELSIUS_TYPE_OF_DEGREE = "CELSIUS";
		public static final String FAHRENHEIT_TYPE_OF_DEGREE = "FAHRENHEIT";
		public static final String IMPERIAL_UNITS = "imperial";
		public static final String METRIC_UNITS = "metric";
	}

}
