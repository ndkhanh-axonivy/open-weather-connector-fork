package com.axonivy.connector.openweather.dto;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.openweathermap.api.data2_5.client.WeatherRecord;

import com.axonivy.connector.openweather.util.Constants;

public class DailyForecast {
	private final LocalDate date;
	private final List<WeatherRecord> dailyRecords;
	private final int temperature;
	private final int minTemperature;
	private final int maxTemperature;
	private final int humidity;
	private final float windSpeed;
	private final int windDirectionDegree;
	private final String weatherIcon;
	private final String weatherDescription;

	public DailyForecast(LocalDate date, List<WeatherRecord> dailyRecords) {
		this.date = date;
		this.dailyRecords = dailyRecords;

		this.temperature = dailyRecords.stream().map(record -> record.getMain().getTemp()).filter(Objects::nonNull)
				.mapToInt(Float::intValue).max().orElse(Constants.Default.WEATHER_TEMPERATURE_DEGREE);

		this.maxTemperature = dailyRecords.stream().map(record -> record.getMain().getTempMax())
				.filter(Objects::nonNull).mapToInt(Float::intValue).max()
				.orElse(Constants.Default.WEATHER_TEMPERATURE_DEGREE);

		this.minTemperature = dailyRecords.stream().map(record -> record.getMain().getTempMin())
				.filter(Objects::nonNull).mapToInt(Float::intValue).min()
				.orElse(Constants.Default.WEATHER_TEMPERATURE_DEGREE);

		this.humidity = dailyRecords.stream().map(record -> record.getMain().getHumidity()).max(Integer::compareTo)
				.orElse(Constants.Default.WEATHER_HUMIDITY);

		this.windSpeed = dailyRecords.stream().map(record -> record.getWind().getSpeed()).max(Float::compareTo)
				.orElse(Constants.Default.WEATHER_WIND_SPEED);
		
		this.windDirectionDegree = dailyRecords.stream().map(record -> record.getWind().getDeg()).max(Integer::compareTo)
				.orElse(Constants.Default.WEATHER_WIND_DEGREE);

		WeatherRecord maxPriorityRecord = dailyRecords.stream()
				.max(Comparator.comparingInt(record -> getWeatherPriority(record.getWeather().get(0).getId())))
				.orElse(null);

		if (maxPriorityRecord != null) {
			this.weatherIcon = maxPriorityRecord.getWeather().get(0).getIcon();
			this.weatherDescription = maxPriorityRecord.getWeather().get(0).getDescription();
		} else {
			this.weatherIcon = Constants.Default.WEATHER_CONDITION_ICON;
			this.weatherDescription = Constants.Default.WEATHER_CONDITION_DESCRIPTION;
		}
	}

	public LocalDate getDate() {
		return date;
	}

	public List<WeatherRecord> getDailyRecords() {
		return dailyRecords;
	}

	public int getTemperature() {
		return temperature;
	}

	public int getMaxTemperature() {
		return maxTemperature;
	}

	public int getMinTemperature() {
		return minTemperature;
	}

	public int getHumidity() {
		return humidity;
	}

	public float getWindSpeed() {
		return windSpeed;
	}

	public int getWindDirectionDegree() {
		return windDirectionDegree;
	}

	public String getWeatherIcon() {
		return weatherIcon;
	}

	public String getWeatherDescription() {
		return weatherDescription;
	}

	private static int getWeatherPriority(int weatherId) {
		int firstDigit = weatherId / 100;
		int lastTwoDigits = weatherId % 100;

		return firstDigit * -100 + lastTwoDigits;
	}
}