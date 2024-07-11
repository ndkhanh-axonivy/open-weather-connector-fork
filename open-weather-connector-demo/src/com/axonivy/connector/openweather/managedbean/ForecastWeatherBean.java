package com.axonivy.connector.openweather.managedbean;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HashMap;
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

import com.axonivy.connector.openweather.dto.DailyForecast;
import com.axonivy.connector.openweather.dto.DailyForecastDisplayInfo;
import com.axonivy.connector.openweather.service.ForecastService;
import com.axonivy.connector.openweather.util.Constants;
import com.axonivy.connector.openweather.util.DateTimeFormatterUtilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ivyteam.ivy.environment.Ivy;

@ManagedBean
@ViewScoped
public class ForecastWeatherBean implements Serializable {

	private static final long serialVersionUID = 4700043015312367231L;

	private String typeOfDegree;
	private LocalDate selectedDate;
	private LocalTime selectedTime;
	private int selectedDateIndex;
	private int selectedTimeIndex;
	private String cityName;
	private String time12HourName;
	private String dateName;
	private ZoneId zoneId;
	private String speedUnit;
	private String units;

	private int currentTemperature;
	private int currentHumidity;
	private float currentWindSpeed;
	private String currentWeatherIconCode;
	private String currentWeatherDetail;

	private List<WeatherRecord> threeHourlyFiveDayForecasts;
	private List<DailyForecast> dailyForecasts;
	private List<DailyForecastDisplayInfo> dailyForecastDisplayInfos;

	private String searchCityName;
	private String searchCountryCode;
	private String searchStateCode;

	private LineChartModel temperatureModel;
	private String temperatureData;
	private BarChartModel precipitationModel;
	private LineChartModel windModel;
	private int chartWindowSize;
	private int currentChartWindowStartX;
	private int currentChartWindowEndX;

	@PostConstruct
	public void init() {
		searchCityName = Ivy.var().get(Constants.Default.SEARCHED_CITY_VARIABLE_PATH);
		units = Ivy.var().get(Constants.Default.UNITS_VARIABLE_PATH);
		typeOfDegree = Constants.Default.TYPE_OF_DEGREE;
		speedUnit = Constants.Default.SPEED_UNIT;
		chartWindowSize = Constants.Default.CHART_WINDOW_SIZE;
		search();
	}

	public LocalDate getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(LocalDate selectedDate) {
		this.selectedDate = selectedDate;
		updateDateName();
	}

	public LocalTime getSelectedTime() {
		return selectedTime;
	}

	public void setSelectedTimeIndexFromUI() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		setSelectedTimeIndex(Integer.parseInt(params.get(Constants.UiVariable.SELECTED_TIME_INDEX_JS_VARIABLE_NAME)));
	}

	public int getSelectedTimeIndex() {
		return selectedTimeIndex;
	}

	public void setSelectedTimeIndex(int selectedTimeIndex) {
		if (selectedTimeIndex < 0 || selectedTimeIndex >= threeHourlyFiveDayForecasts.size()) {
			return;
		}
		this.selectedTimeIndex = selectedTimeIndex;
		WeatherRecord selectedRecord = threeHourlyFiveDayForecasts.get(selectedTimeIndex);
		LocalDateTime dateTime = Instant.ofEpochSecond(selectedRecord.getDt()).atZone(zoneId).toLocalDateTime();

		setSelectedTime(dateTime.toLocalTime());

		if (selectedDate.isAfter(dateTime.toLocalDate())) {
			setSelectedDate(dateTime.toLocalDate());
			selectedDateIndex--;
		} else if (selectedDate.isBefore(dateTime.toLocalDate())) {
			setSelectedDate(dateTime.toLocalDate());
			selectedDateIndex++;
		}

		currentTemperature = selectedRecord.getMain().getTemp().intValue();
		currentHumidity = selectedRecord.getMain().getHumidity();
		currentWindSpeed = selectedRecord.getWind().getSpeed();
		currentWeatherIconCode = selectedRecord.getWeather().get(0).getIcon();
		currentWeatherDetail = StringUtils.capitalize(selectedRecord.getWeather().get(0).getDescription());
	}

	public void setSelectedTime(LocalTime selectedTime) {
		this.selectedTime = selectedTime;
		updateTime12HourName();
	}

	public int getSelectedDateIndex() {
		return selectedDateIndex;
	}

	public void setSelectedDateIndex(int selectedDateIndex) {
		if (selectedDateIndex < 0 || selectedDateIndex >= dailyForecastDisplayInfos.size()) {
			return;
		}

		this.selectedDateIndex = selectedDateIndex;
		DailyForecast selectedForecast = dailyForecastDisplayInfos.get(selectedDateIndex).getDailyForecast();
		setSelectedDate(selectedForecast.getDate());

		currentTemperature = selectedForecast.getTemperature();
		currentHumidity = selectedForecast.getHumidity();
		currentWindSpeed = selectedForecast.getWindSpeed();
		currentWeatherIconCode = selectedForecast.getWeatherIcon();
		currentWeatherDetail = StringUtils.capitalize(selectedForecast.getWeatherDescription());
		currentChartWindowStartX = prepareCurrentChartWindowStartX();
		currentChartWindowEndX = prepareCurrentChartWindowEndX();

		setSelectedTime(null);
	}

	public String getTypeOfDegree() {
		return typeOfDegree;
	}

	public void setTypeOfDegree(String typeOfDegree) {
		if (!Objects.equals(this.typeOfDegree, typeOfDegree)) {
			this.typeOfDegree = typeOfDegree;
			if (typeOfDegree.equals(Constants.OpenWeatherMapVariable.CELSIUS_TYPE_OF_DEGREE)) {
				speedUnit = Constants.UiDisplay.SPEED_METER_UNIT;
				units = Constants.OpenWeatherMapVariable.METRIC_UNITS;
			} else if (typeOfDegree.equals(Constants.OpenWeatherMapVariable.FAHRENHEIT_TYPE_OF_DEGREE)) {
				speedUnit = Constants.UiDisplay.SPEED_MILE_UNIT;
				units = Constants.OpenWeatherMapVariable.IMPERIAL_UNITS;
			} else {
				typeOfDegree = StringUtils.EMPTY;
				speedUnit = StringUtils.EMPTY;
				units = StringUtils.EMPTY;
			}
			processAndGroupForecastData();
			if (selectedTime == null) {
				setSelectedDateIndex(selectedDateIndex);
			} else {
				setSelectedTimeIndex(selectedTimeIndex);
			}
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

	public String getTime12HourName() {
		return time12HourName;
	}

	public String getDateName() {
		return dateName;
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

	private void updateDateName() {
		dateName = DateTimeFormatterUtilities.formatDate(selectedDate);
	}

	private void updateTime12HourName() {
		time12HourName = DateTimeFormatterUtilities.formatTime12Hour(selectedTime);
	}

	public String getCityName() {
		return cityName;
	}

	public List<DailyForecastDisplayInfo> getDailyForecastDisplayInfos() {
		return dailyForecastDisplayInfos;
	}

	public LineChartModel getTemperatureModel() {
		return temperatureModel;
	}

	public String getUnits() {
		return units;
	}

	public String getSpeedUnit() {
		return speedUnit;
	}

	public BarChartModel getPrecipitationModel() {
		return precipitationModel;
	}

	public LineChartModel getWindModel() {
		return windModel;
	}

	public void setWindModel(LineChartModel windModel) {
		this.windModel = windModel;
	}

	public int getChartWindowSize() {
		return chartWindowSize;
	}

	public int getCurrentChartWindowStartX() {
		return currentChartWindowStartX;
	}

	public void setCurrentChartWindowStartX(int currentChartWindowStartX) {
		this.currentChartWindowStartX = currentChartWindowStartX;
	}

	public int getCurrentChartWindowEndX() {
		return currentChartWindowEndX;
	}

	public void setCurrentChartWindowEndX(int currentChartWindowEndX) {
		this.currentChartWindowEndX = currentChartWindowEndX;
	}

	public int prepareCurrentChartWindowStartX() {
		return dailyForecastDisplayInfos.get(selectedDateIndex).getChartWindowStartX();
	}

	public int prepareCurrentChartWindowEndX() {
		return dailyForecastDisplayInfos.get(selectedDateIndex).getChartWindowEndX();
	}

	public void search() {
		processAndGroupForecastData();
		setSelectedDateIndex(0);
		createTemperatureModel();
		createPrecipitationModel();
		createWindModel();
	}

	private void processAndGroupForecastData() {
		Optional<Forecast> optionalForecast = ForecastService.getInstance()
				.fetchForecastThreeHourlyFiveDay(searchCityName, searchCountryCode, searchStateCode, units);

		if (optionalForecast.isEmpty()) {
			return;
		}
		Forecast forecast = optionalForecast.get();

		forecast.getList().forEach(record -> {
			record.getWeather().forEach(weather -> {
				String icon = weather.getIcon();
				if (icon != null) {
					icon = icon.replace(Constants.OpenWeatherMapVariable.NOTATION_NIGHT,
							Constants.OpenWeatherMapVariable.NOTATION_DAY);
					weather.setIcon(icon);
				}
			});
		});

		zoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(forecast.getCity().getTimezone()));
		cityName = forecast.getCity().getName();
		threeHourlyFiveDayForecasts = forecast.getList();
		groupForecastData();
		temperatureData = prepareTemperatureData().toString();
	}

	private void groupForecastData() {
		Map<LocalDate, List<WeatherRecord>> forecastsByDate = groupForecastsByDate();

		dailyForecasts = generateDailyForecasts(forecastsByDate);

		dailyForecastDisplayInfos = IntStream.range(0, dailyForecasts.size())
				.mapToObj(index -> createDailyForecastDisplayInfo(index)).collect(Collectors.toList());
	}

	private Map<LocalDate, List<WeatherRecord>> groupForecastsByDate() {
		return threeHourlyFiveDayForecasts.stream().collect(
				Collectors.groupingBy(record -> Instant.ofEpochSecond(record.getDt()).atZone(zoneId).toLocalDate()));
	}

	private List<DailyForecast> generateDailyForecasts(Map<LocalDate, List<WeatherRecord>> forecastsByDate) {
		return forecastsByDate.entrySet().stream().map(entry -> new DailyForecast(entry.getKey(), entry.getValue()))
				.sorted(Comparator.comparing(DailyForecast::getDate)).limit(5).collect(Collectors.toList());
	}

	private DailyForecastDisplayInfo createDailyForecastDisplayInfo(int index) {
		int startX = calculateModelStartX(index);
		int endX = startX + chartWindowSize - 1;
		return new DailyForecastDisplayInfo(dailyForecasts.get(index), startX, endX);
	}

	private int calculateModelStartX(int index) {
		return IntStream.range(0, index).map(i -> dailyForecasts.get(i).getDailyRecords().size()).sum();
	}

	private void createTemperatureModel() {
		temperatureModel = new LineChartModel();
		LineChartOptions options = new LineChartOptions();
		temperatureModel.setOptions(options);
		temperatureModel.setData(prepareTemperatureChartData());
		temperatureModel.setExtender(Constants.UiVariable.TEMPERATURE_CHART_EXTENDER_JS_METHOD_NAME);
	}

	public ChartData prepareTemperatureChartData() {
		ChartData data = new ChartData();
		LineChartDataSet dataSet = new LineChartDataSet();
		dataSet.setLabel(Constants.Chart.TEMPERATURE_DATASET_LABEL);
		dataSet.setFill(false);
		dataSet.setTension(0.4);
		dataSet.setData(prepareTemperatureData());
		data.setLabels(prepareTimeLabels());
		data.addChartDataSet(dataSet);
		return data;
	}

	public ChartData preparePrecipitationChartData() {
		ChartData data = new ChartData();
		BarChartDataSet dataSet = new BarChartDataSet();
		dataSet.setLabel(Constants.Chart.PRECIPITATION_DATASET_LABEL);
		dataSet.setData(preparePrecipitationData());
		data.setLabels(prepareTimeLabels());
		data.addChartDataSet(dataSet);
		return data;
	}

	public void createWindModel() {
		windModel = new LineChartModel();
		LineChartOptions options = new LineChartOptions();
		windModel.setData(prepareWindChartData());
		windModel.setOptions(options);
		windModel.setExtender(Constants.UiVariable.WIND_CHART_EXTENDER_JS_METHOD_NAME);
	}

	public ChartData prepareWindChartData() {
		ChartData data = new ChartData();
		LineChartDataSet dataSet = new LineChartDataSet();
		dataSet.setLabel(Constants.Chart.WIND_DATASET_LABEL);
		dataSet.setData(prepareWindData());
		dataSet.setCubicInterpolationMode(prepareCustomWindData());
		data.setLabels(prepareTimeLabels());
		data.addChartDataSet(dataSet);
		return data;
	}

	private String prepareCustomWindData() {
		List<Map<String, String>> result = dailyForecastDisplayInfos.stream()
				.map(DailyForecastDisplayInfo::getDailyForecast)
				.flatMap(dailyForecast -> dailyForecast.getDailyRecords().stream().map(record -> {
					Map<String, String> data = new HashMap<>();
					data.put("speed", record.getWind().getSpeed().toString() + " " + speedUnit);
					data.put("deg", record.getWind().getDeg().toString());
					return data;
				})).collect(Collectors.toList());

		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			return StringUtils.EMPTY;
		}
	}

	public List<Object> prepareWindData() {
		return dailyForecastDisplayInfos.stream().map(DailyForecastDisplayInfo::getDailyForecast)
				.flatMap(dailyForecast -> dailyForecast.getDailyRecords().stream().map(record -> 10))
				.collect(Collectors.toList());
	}

	public List<Object> prepareTemperatureData() {
		return dailyForecastDisplayInfos
				.stream().map(DailyForecastDisplayInfo::getDailyForecast).flatMap(dailyForecast -> dailyForecast
						.getDailyRecords().stream().map(record -> record.getMain().getTempMax().intValue()))
				.collect(Collectors.toList());
	}

	public List<String> prepareTimeLabels() {
		return dailyForecastDisplayInfos.stream().map(DailyForecastDisplayInfo::getDailyForecast)
				.flatMap(dailyForecast -> dailyForecast.getDailyRecords().stream()
						.map(record -> Instant.ofEpochSecond(record.getDt()).atZone(zoneId).toLocalTime()))
				.map(DateTimeFormatterUtilities::formatTime24Hour).collect(Collectors.toList());
	}

	public void createPrecipitationModel() {
		precipitationModel = new BarChartModel();
		BarChartOptions options = new BarChartOptions();
		precipitationModel.setOptions(options);
		precipitationModel.setData(preparePrecipitationChartData());
		precipitationModel.setExtender(Constants.UiVariable.PRECIPITATION_CHART_EXTENDER_JS_METHOD_NAME);
	}

	public List<Number> preparePrecipitationData() {
		return dailyForecastDisplayInfos.stream().map(DailyForecastDisplayInfo::getDailyForecast).flatMap(
				dailyForecast -> dailyForecast.getDailyRecords().stream().map(record -> (int) (record.getPop() * 100)))
				.collect(Collectors.toList());
	}

	public String getTemperatureData() {
		return temperatureData;
	}

	public void setTemperatureData(String temperatureData) {
		this.temperatureData = temperatureData;
	}
}
