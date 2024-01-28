package com.axonivy.connector.openweather.managedbean;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.axonivy.connector.openweather.data.forecast.ForecastWeatherRecord;
import com.axonivy.connector.openweather.data.forecast.ForecastWeatherResponse;
import com.axonivy.connector.openweather.enums.TypeOfDegree;
import com.axonivy.connector.openweather.exception.OpenWeatherMapException;
import com.axonivy.connector.openweather.util.DateTimeFormatterUtilities;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.call.SubProcessCall;
import ch.ivyteam.ivy.process.call.SubProcessCallResult;

@ManagedBean
@ViewScoped
public class ForecastWeatherBean implements Serializable {

	private static final long serialVersionUID = 4700043015312367231L;

	private TypeOfDegree typeOfDegree;
	private LocalDate selectedDate;
	private LocalTime selectedTime;
	private int dateIndex;
	private String cityName;
	private String formattedTime12Hour;
	private String formattedDate;

	private int currentTemperature;
	private int currentHumidity;
	private float currentWindSpeed;
	private String currentWeatherIconCode;
	private String currentWeatherDetail;

	private List<DailyForecast> dailyForecasts;

	private String searchCityName;
	private String searchCountryCode;
	private String searchStateCode;

	@PostConstruct
	public void init() {
		searchCityName = "New York";
		searchCountryCode = "US";
		searchStateCode = "NY";
		search();
	}

	public LocalDate getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDateByJsf() {

		String selectedDateStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("selectedDate");
		Ivy.log().info(selectedDateStr);
		if (selectedDateStr != null && !selectedDateStr.isEmpty()) {
			LocalDate selectedDate = LocalDate.parse(selectedDateStr);
			setSelectedDate(selectedDate);
			// Now 'selectedDate' contains the parsed LocalDate value
		} else {
			// Handle the case where 'selectedDateStr' is null or empty
		}
	}

	public void setSelectedDate(LocalDate selectedDate) {
		Ivy.log().info(selectedDate.toString());
		this.selectedDate = selectedDate;
		updateFormattedDate();
		setSelectedTime(null);

		dateIndex = IntStream.range(0, dailyForecasts.size())
				.filter(i -> dailyForecasts.get(i).getDate().equals(selectedDate)).findFirst().orElse(-1);

		if (dateIndex != -1) {
			// Selected date found, use the index to get the selectedDailyForecast
			DailyForecast selectedForecast = dailyForecasts.get(dateIndex);
			currentTemperature = selectedForecast.getTemperature();
			currentHumidity = selectedForecast.getHumidity();
			currentWindSpeed = selectedForecast.getWindSpeed();
			currentWeatherIconCode = selectedForecast.getWeatherIcon();
			currentWeatherDetail = StringUtils.capitalize(selectedForecast.getWeatherDescription());
		}
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

	public int getCurrentWeatherDegree() {
		return currentTemperature;
	}

	public int getCurrentHumidity() {
		return currentHumidity;
	}

	public float getCurrentWindSpeed() {
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

	public String getSearchCityName() {
		return searchCityName;
	}

	public void setSearchCityName(String searchCityName) {
		this.searchCityName = searchCityName;
	}

	public String getSearchCountryCode() {
		return searchCountryCode;
	}

	public void setSearchCountryCode(String searchCountryCode) {
		this.searchCountryCode = searchCountryCode;
	}

	public String getSearchStateCode() {
		return searchStateCode;
	}

	public void setSearchStateCode(String searchStateCode) {
		this.searchStateCode = searchStateCode;
	}

	private void updateFormattedDate() {
		formattedDate = DateTimeFormatterUtilities.formatDate(selectedDate);
	}

	private void updateFormattedTime12Hour() {
		formattedTime12Hour = DateTimeFormatterUtilities.formatTime12Hour(selectedTime);
	}

	public String getCityName() {
		return cityName;
	}

	public List<DailyForecast> getDailyForecasts() {
		return dailyForecasts;
	}
	
	public int getDateIndex() {
		return dateIndex;
	}

	public void search() {
		ForecastWeatherResponse forecastWeather = null;
		Ivy.log().info("searchCityName: " + searchCityName);
		SubProcessCallResult callResult = SubProcessCall.withPath("ForecastWeather").withStartName("call")
				.withParam("cityName", searchCityName).withParam("countryCode", searchCountryCode)
				.withParam("stateCode", searchStateCode).call();
		if (callResult != null) {
			Optional<Object> o = Optional.ofNullable(callResult.get("response"));
			if (o.isPresent() && o.get() instanceof ForecastWeatherResponse object) {
				forecastWeather = (ForecastWeatherResponse) object;
			}
		}
		cityName = forecastWeather.getCity().getName();
		List<ForecastWeatherRecord> threeHourlyFiveDayForecasts = forecastWeather.getList();
		Ivy.log().info(threeHourlyFiveDayForecasts.size());
		// Group forecasts by date
		Map<LocalDate, List<ForecastWeatherRecord>> forecastsByDate = threeHourlyFiveDayForecasts.stream()
				.collect(Collectors.groupingBy(
						record -> Instant.ofEpochSecond(record.getDt()).atZone(ZoneId.systemDefault()).toLocalDate()));

		dailyForecasts = forecastsByDate.entrySet().stream()
				.map(entry -> new DailyForecast(entry.getKey(), entry.getValue()))
				.sorted(Comparator.comparing(DailyForecast::getDate)).limit(5).collect(Collectors.toList());

		dateIndex = 0;
		setSelectedDate(dailyForecasts.get(dateIndex).date);
	}

	public static class DailyForecast {
		private final LocalDate date;
		private String formattedEEE;
		private final List<ForecastWeatherRecord> dailyRecords;
		private final int temperature;
		private final int minTemperature;
		private final int maxTemperature;
		private final int humidity;
		private final float windSpeed;
		private final String weatherIcon;
		private final String weatherDescription;
		private static final String DEFAULT_WEATHER_CONDITION_ICON = "01d";
		private static final String DEFAULT_WEATHER_CONDITION_DESCRIPTION = "clear sky";

		private DailyForecast(LocalDate date, List<ForecastWeatherRecord> dailyRecords) {
			this.date = date;
			this.dailyRecords = dailyRecords;

			formattedEEE = DateTimeFormatterUtilities.formatEEE(date);
			temperature = dailyRecords.stream().map(ForecastWeatherRecord::getMainTemp).mapToInt(Float::intValue).max()
					.orElse(0);
			maxTemperature = dailyRecords.stream().map(ForecastWeatherRecord::getMainMaxTemp).mapToInt(Float::intValue)
					.max().orElse(0);
			minTemperature = dailyRecords.stream().map(ForecastWeatherRecord::getMainMinTemp).mapToInt(Float::intValue)
					.min().orElse(0);
			humidity = dailyRecords.stream().map(ForecastWeatherRecord::getMainHumidity).max(Integer::compareTo)
					.orElse(0);
			windSpeed = dailyRecords.stream().map(ForecastWeatherRecord::getWindSpeed).max(Float::compareTo)
					.orElse(0.0f);
			weatherIcon = dailyRecords.stream()
					.max(Comparator.comparingInt(record -> getWeatherPriority(record.getWeatherId())))
					.map(ForecastWeatherRecord::getWeatherIcon).orElse(DEFAULT_WEATHER_CONDITION_ICON);
			weatherDescription = dailyRecords.stream()
					.max(Comparator.comparingInt(record -> getWeatherPriority(record.getWeatherId())))
					.map(ForecastWeatherRecord::getWeatherDescription).orElse(DEFAULT_WEATHER_CONDITION_DESCRIPTION);
		}

		public LocalDate getDate() {
			return date;
		}

		public List<ForecastWeatherRecord> getDailyRecords() {
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

		public String getWeatherIcon() {
			return weatherIcon;
		}

		public String getWeatherDescription() {
			return weatherDescription;
		}

		public String getFormattedEEE() {
			return formattedEEE;
		}

		private static int getWeatherPriority(int weatherId) {
			int firstDigit = weatherId / 100;
			int lastTwoDigits = weatherId % 100;

			return firstDigit * -100 + lastTwoDigits;
		}
	}
}
