package com.axonivy.connector.openweather.managedbean;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.openweathermap.api.data2_5.client.Forecast;
import org.openweathermap.api.data2_5.client.WeatherRecord;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;

import com.axonivy.connector.openweather.util.DateTimeFormatterUtilities;

import ch.ivyteam.ivy.process.call.SubProcessCall;
import ch.ivyteam.ivy.process.call.SubProcessCallResult;

@ManagedBean
@ViewScoped
public class ForecastWeatherBean implements Serializable {

	private static final long serialVersionUID = 4700043015312367231L;

	private String typeOfDegree;
	private LocalDate selectedDate;
	private LocalTime selectedTime;
	private int dateIndex;
	private String cityName;
	private String formattedTime12Hour;
	private String formattedDate;
	private ZoneId zoneId;
	private String speedUnit;
	
	private int currentTemperature;
	private int currentHumidity;
	private float currentWindSpeed;
	private String currentWeatherIconCode;
	private String currentWeatherDetail;

	private List<DailyForecast> dailyForecasts;

	private String searchCityName;
	private String searchCountryCode;
	private String searchStateCode;
	private String units;

	private LineChartModel lineModelForTemperature;
	private BarChartModel barModelForPrecipitation;

	@PostConstruct
	public void init() {
		searchCityName = "New York";
		units = "metric";
		typeOfDegree = "CELSIUS";
		speedUnit = "m/s";
		search();
	}

	public LocalDate getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(LocalDate selectedDate) {
		this.selectedDate = selectedDate;
		updateFormattedDate();
		setSelectedTime(null);

		dateIndex = IntStream.range(0, dailyForecasts.size())
				.filter(i -> dailyForecasts.get(i).getDate().equals(selectedDate)).findFirst().orElse(-1);

		if (dateIndex != -1) {
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

	public void setSelectedTimeByIndex() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		int index = Integer.parseInt(params.get("index"));

		if (dateIndex >= 0 && dateIndex < dailyForecasts.size()) {

			List<WeatherRecord> forecastWeatherRecords = dailyForecasts.stream()
					.flatMap(dailyForecast -> dailyForecast.getDailyRecords().stream()).collect(Collectors.toList());

			WeatherRecord selectedRecord = forecastWeatherRecords.get(index);
			LocalDateTime dateTime = Instant.ofEpochSecond(selectedRecord.getDt()).atZone(zoneId).toLocalDateTime();
			setSelectedTime(dateTime.toLocalTime());
			selectedDate = dateTime.toLocalDate();
			updateFormattedDate();
			currentTemperature = selectedRecord.getMain().getTemp().intValue();
			currentHumidity = selectedRecord.getMain().getHumidity();
			currentWindSpeed = selectedRecord.getWind().getSpeed();
			currentWeatherIconCode = selectedRecord.getWeather().get(0).getIcon();
			currentWeatherDetail = StringUtils.capitalize(selectedRecord.getWeather().get(0).getDescription());
		}
	}

	public void setSelectedTime(LocalTime selectedTime) {
		this.selectedTime = selectedTime;
		updateFormattedTime12Hour();
	}

	public String getTypeOfDegree() {
		return typeOfDegree;
	}

	public void setTypeOfDegree(String typeOfDegree) {
		if (!Objects.equals(this.typeOfDegree, typeOfDegree)) {
	        this.typeOfDegree = typeOfDegree;
	        if (typeOfDegree.equals("CELSIUS")) {
	        	speedUnit = "m/s";
	        	units = "metric";
	        }
	        else if (typeOfDegree.equals("FAHRENHEIT")) {
	        	speedUnit = "mph";
	        	units = "imperial";
	        }
	        else {
	        	typeOfDegree = "";
	        	speedUnit = "";
	        	units = "";
	        }
	        search();
	    }
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

	public String getCurrentWeatherIconCode() {
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

	public LineChartModel getLineModelForTemperature() {
		return lineModelForTemperature;
	}

	public String getUnits() {
		return units;
	}
	
	public String getSpeedUnit() {
		return speedUnit;
	}
	
	public BarChartModel getBarModelForPrecipitation() {
		return barModelForPrecipitation;
	}

	public void search() {
		Optional<Forecast> optionalForecast = Optional.ofNullable(fetchForecast());

		optionalForecast.ifPresent(forecast -> {
			forecast.getList().forEach(record -> {
				record.getWeather().forEach(weather -> {
					String icon = weather.getIcon();
					if (icon != null) {
						icon = icon.replace('n', 'd');
						weather.setIcon(icon);
					}
				});
			});
			zoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(forecast.getCity().getTimezone()));
			cityName = forecast.getCity().getName();
			List<WeatherRecord> threeHourlyFiveDayForecasts = forecast.getList();

			// Group forecasts by date
			Map<LocalDate, List<WeatherRecord>> forecastsByDate = threeHourlyFiveDayForecasts.stream()
					.collect(Collectors.groupingBy(record -> Instant.ofEpochSecond(record.getDt())
							.atZone(zoneId).toLocalDate()));

			dailyForecasts = forecastsByDate.entrySet().stream()
					.map(entry -> new DailyForecast(entry.getKey(), entry.getValue()))
					.sorted(Comparator.comparing(DailyForecast::getDate)).limit(5).collect(Collectors.toList());

			dateIndex = 0;
			setSelectedDate(dailyForecasts.get(dateIndex).getDate());
			createLineModelForTemperature();
			createBarModelForPrecipitation();
		});
	}

	private Forecast fetchForecast() {
		SubProcessCallResult callResult = SubProcessCall.withPath("connector/ForecastWeather")
				.withStartName("getForecastWeatherByLocationName").withParam("cityName", searchCityName)
				.withParam("countryCode", searchCountryCode).withParam("stateCode", searchStateCode)
				.withParam("units", units).call();

		if (callResult != null) {
			Optional<Object> o = Optional.ofNullable(callResult.get("forecastWeather"));
			if (o.isPresent() && o.get() instanceof Forecast) {
				return (Forecast) o.get();
			}
		}
		return null;
	}

	public void createLineModelForTemperature() {
		lineModelForTemperature = new LineChartModel();
		ChartData data = new ChartData();

		List<LocalTime> timeLabels = dailyForecasts.stream()
				.flatMap(dailyForecast -> dailyForecast.getDailyRecords().stream().map(
						record -> Instant.ofEpochSecond(record.getDt()).atZone(zoneId).toLocalTime()))
				.collect(Collectors.toList());

		List<Object> values = dailyForecasts.stream().flatMap(dailyForecast -> dailyForecast.getDailyRecords().stream()
				.map(record -> record.getMain().getTempMax().intValue())).collect(Collectors.toList());

		LineChartDataSet dataSet = new LineChartDataSet();
		dataSet.setData(values);
		dataSet.setLabel("Temperature");
		dataSet.setFill(false);
		dataSet.setTension(0.4);
		data.addChartDataSet(dataSet);

		// Set time labels
		List<String> labels = timeLabels.stream().map(time -> DateTimeFormatterUtilities.formatTime24Hour(time))
				.collect(Collectors.toList());

		data.setLabels(labels);

		LineChartOptions options = new LineChartOptions();

		lineModelForTemperature.setOptions(options);
		lineModelForTemperature.setData(data);
		lineModelForTemperature.setExtender("temperatureChartExtender");
	}
	
	public void createBarModelForPrecipitation() {
		barModelForPrecipitation = new BarChartModel();
		ChartData data = new ChartData();

		List<LocalTime> timeLabels = dailyForecasts.stream()
				.flatMap(dailyForecast -> dailyForecast.getDailyRecords().stream().map(
						record -> Instant.ofEpochSecond(record.getDt()).atZone(zoneId).toLocalTime()))
				.collect(Collectors.toList());

		List<Number> values = dailyForecasts.stream().flatMap(dailyForecast -> dailyForecast.getDailyRecords().stream()
				.map(record -> record.getPop().intValue())).collect(Collectors.toList());

		BarChartDataSet dataSet = new BarChartDataSet();
		dataSet.setData(values);
		dataSet.setLabel("Precipitation");
		data.addChartDataSet(dataSet);

		// Set time labels
		List<String> labels = timeLabels.stream().map(time -> DateTimeFormatterUtilities.formatTime24Hour(time))
				.collect(Collectors.toList());

		data.setLabels(labels);

		BarChartOptions options = new BarChartOptions();

		barModelForPrecipitation.setOptions(options);
		barModelForPrecipitation.setData(data);
		barModelForPrecipitation.setExtender("precipitationChartExtender");
	}

	public static class DailyForecast {
		private final LocalDate date;
		private String formattedEEE;
		private final List<WeatherRecord> dailyRecords;
		private final int temperature;
		private final int minTemperature;
		private final int maxTemperature;
		private final int humidity;
		private final float windSpeed;
		private final String weatherIcon;
		private final String weatherDescription;
		private final int sizeDaily;
		private static final String DEFAULT_WEATHER_CONDITION_ICON = "01d";
		private static final String DEFAULT_WEATHER_CONDITION_DESCRIPTION = "clear sky";

		private DailyForecast(LocalDate date, List<WeatherRecord> dailyRecords) {
			this.date = date;
			this.dailyRecords = dailyRecords;
			sizeDaily = dailyRecords.size();
			formattedEEE = DateTimeFormatterUtilities.formatEEE(date);
			temperature = dailyRecords.stream().map(record -> record.getMain().getTemp()).filter(Objects::nonNull)
					.mapToInt(Float::intValue).max().orElse(-999);
			maxTemperature = dailyRecords.stream().map(record -> record.getMain().getTempMax()).filter(Objects::nonNull)
					.mapToInt(Float::intValue).max().orElse(-999);
			minTemperature = dailyRecords.stream().map(record -> record.getMain().getTempMin()).filter(Objects::nonNull)
					.mapToInt(Float::intValue).max().orElse(-999);
			humidity = dailyRecords.stream().map(record -> record.getMain().getHumidity()).max(Integer::compareTo)
					.orElse(0);
			windSpeed = dailyRecords.stream().map(record -> record.getWind().getSpeed()).max(Float::compareTo)
					.orElse(0.0f);
			weatherIcon = dailyRecords.stream()
					.max(Comparator.comparingInt(record -> getWeatherPriority(record.getWeather().get(0).getId())))
					.map(record -> record.getWeather().get(0).getIcon()).orElse(DEFAULT_WEATHER_CONDITION_ICON);
			weatherDescription = dailyRecords.stream()
					.max(Comparator.comparingInt(record -> getWeatherPriority(record.getWeather().get(0).getId())))
					.map(record -> record.getWeather().get(0).getDescription())
					.orElse(DEFAULT_WEATHER_CONDITION_DESCRIPTION);
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

		public String getWeatherIcon() {
			return weatherIcon;
		}

		public String getWeatherDescription() {
			return weatherDescription;
		}

		public String getFormattedEEE() {
			return formattedEEE;
		}

		public int getSizeDaily() {
			return sizeDaily;
		}

		private static int getWeatherPriority(int weatherId) {
			int firstDigit = weatherId / 100;
			int lastTwoDigits = weatherId % 100;

			return firstDigit * -100 + lastTwoDigits;
		}

	}
}
