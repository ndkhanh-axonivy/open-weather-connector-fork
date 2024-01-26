package com.axonivy.connector.openweather.managedbean;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.ObjectUtils;

import com.axonivy.connector.openweather.data.forecast.ForecastWeatherRecord;
import com.axonivy.connector.openweather.data.forecast.ForecastWeatherResponse;
import com.axonivy.connector.openweather.enums.TypeOfDegree;
import com.axonivy.connector.openweather.exception.OpenWeatherMapException;
import com.axonivy.connector.openweather.util.DateTimeFormatterUtilities;
import com.axonivy.connector.openweather.util.FacesContexts;

@ManagedBean
@ViewScoped
public class ForecastWeatherBean implements Serializable {

	private static final long serialVersionUID = 4700043015312367231L;

	private TypeOfDegree typeOfDegree;
	private LocalDate selectedDate;
	private LocalTime selectedTime;
	private String formattedTime12Hour;
	private String formattedDate;
	private Float currentWeatherDegree;
	private Float currentHumidity;
	private Float currentWindSpeed;
	private String currentWeatherIconCode;
	private String currentWeatherDetail;
	private List<ForecastWeatherRecord> threeHourlyFiveDayForecasts;
	private List<DailyForecast> dailyForecasts;

	@PostConstruct
	public void init() {
		try {
			threeHourlyFiveDayForecasts = FacesContexts.evaluateValueExpression(
					"#{data.forecastWeather.forecastWeatherResponse}", ForecastWeatherResponse.class).getList();
		} catch (OpenWeatherMapException e) {
			// Just continue to initialize
		}
	}

	public LocalDate getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(LocalDate selectedDate) {
		this.selectedDate = selectedDate;
		updateFormattedDate();
		setSelectedTime(null);
	}

	public LocalTime getSelectedTime() {
		return selectedTime;
	}

	public void setSelectedTime(LocalTime selectedTime) {
		this.selectedTime = selectedTime;
		updateFormattedTime12Hour();
	}

	public TypeOfDegree getTypeOfDegree() {
		return typeOfDegree;
	}

	public Float getCurrentWeatherDegree() {
		return currentWeatherDegree;
	}

	public Float getCurrentHumidity() {
		return currentHumidity;
	}

	public Float getCurrentWindSpeed() {
		return currentWindSpeed;
	}

	public String getCurrentWeatherIcon() {
		return currentWeatherIconCode;
	}

	public String getCurrentWeatherDetail() {
		return currentWeatherDetail;
	}

	public String getFormattedTime12Hour() {
		return formattedTime12Hour;
	}

	public String getFormattedDate() {
		return formattedDate;
	}

	private void updateFormattedDate() {
		formattedDate = DateTimeFormatterUtilities.formatDate(selectedDate);
	}

	private void updateFormattedTime12Hour() {
		formattedTime12Hour = DateTimeFormatterUtilities.formatTime12Hour(selectedTime);
	}

	private void calculateDailyForecasts() {
		if (ObjectUtils.isEmpty(threeHourlyFiveDayForecasts)) {
			return;
		}

		// Group forecasts by date
		Map<LocalDate, List<ForecastWeatherRecord>> forecastsByDate = threeHourlyFiveDayForecast.stream()
				.collect(Collectors.groupingBy(record -> record.getDt().toLocalDate()));

		// Calculate daily forecasts
		dailyForecasts = new ArrayList<>();
		for (Map.Entry<LocalDate, List<ForecastWeatherRecord>> entry : forecastsByDate.entrySet()) {
			LocalDate date = entry.getKey();
			List<ForecastWeatherRecord> dailyRecords = entry.getValue();

			float maxTemperature = dailyRecords.stream().map(ForecastWeatherRecord::getMain).map(Main::getTempMax)
					.max(Float::compareTo).orElse(0.0f);

			float maxHumidity = dailyRecords.stream().map(ForecastWeatherRecord::getMain).map(Main::getHumidity)
					.max(Float::compareTo).orElse(0.0f);

			dailyForecasts.add(new DailyForecast(date, dailyRecords, maxTemperature, maxHumidity));
		}
	}

	private static class DailyForecast {
		private final LocalDate date;
		private final List<ForecastWeatherRecord> dailyRecords;
		private final float maxTemperature;
		private final float maxHumidity;

		private DailyForecast(LocalDate date, List<ForecastWeatherRecord> dailyRecords, float maxTemperature,
				float maxHumidity) {
			this.date = date;
			this.dailyRecords = dailyRecords;
			this.maxTemperature = maxTemperature;
			this.maxHumidity = maxHumidity;
		}

		// Getters for properties
	}
}
