package com.axonivy.connector.openweather.service;

import java.util.Optional;

import org.openweathermap.api.data2_5.client.Forecast;

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
		SubProcessCallResult callResult = SubProcessCall.withPath("connector/ForecastWeather")
				.withStartName("getForecastWeatherByLocationName").withParam("cityName", searchCityName)
				.withParam("countryCode", searchCountryCode).withParam("stateCode", searchStateCode)
				.withParam("units", units).call();

		if (callResult != null) {
			Object forecastWeather = callResult.get("forecastWeather");
			if (forecastWeather instanceof Forecast) {
				return Optional.of((Forecast) forecastWeather);
			}
		}
		return Optional.empty();
	}
}
