package com.axonivy.connector.openweather.service;

import java.util.Optional;

import org.openweathermap.api.data2_5.client.Forecast;

import com.axonivy.connector.openweather.util.Constants;

import ch.ivyteam.ivy.process.call.SubProcessCall;
import ch.ivyteam.ivy.process.call.SubProcessCallResult;

public class ForecastService {

	private static ForecastService instance;

	public static ForecastService getInstance() {
		if (instance == null) {
			instance = new ForecastService();
		}
		return instance;
	}

	public Optional<Forecast> fetchForecastThreeHourlyFiveDay(String searchCityName, String searchCountryCode,
			String searchStateCode, String units) {
		SubProcessCallResult callResult = SubProcessCall.withPath(Constants.UiVariable.FORECAST_WEATHER_CONNECTOR)
				.withStartName(Constants.UiVariable.GET_FORECAST_WEATHER_BY_LOCATION_START_NAME)
				.withParam(Constants.UiVariable.CITY_NAME_PARAM_NAME, searchCityName)
				.withParam(Constants.UiVariable.COUNTRY_CODE_PARAM_NAME, searchCountryCode)
				.withParam(Constants.UiVariable.STATE_CODE_PARAM_NAME, searchStateCode)
				.withParam(Constants.UiVariable.UNITS_PARAM_NAME, units).call();

		if (callResult != null) {
			Object forecastWeather = callResult.get(Constants.UiVariable.FORECAST_WEATHER_RESULT_NAME);
			if (forecastWeather instanceof Forecast) {
				return Optional.of((Forecast) forecastWeather);
			}
		}
		return Optional.empty();
	}
}
