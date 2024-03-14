package com.axonivy.connector.openweather.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openweathermap.api.data2_5.client.Forecast;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.error.BpmError;

public class ForecastWeatherProcessTest extends BaseProcessTest {

	private static final BpmProcess GET_FORECAST_PROCESS = BpmProcess.path("connector/ForecastWeather");
	private static final BpmElement GET_FORECAST_BY_GEOCODE = GET_FORECAST_PROCESS
			.elementName("getForecastWeather(Double,Double,Integer,String,String)");

	@Test
	public void testGetForecastWeatherByGeoCode_ReturnsForecast(BpmClient bpmClient) throws NoSuchFieldException {
		ExecutionResult result = bpmClient.start().subProcess(GET_FORECAST_BY_GEOCODE).execute(40.7127281, -74.0060152, 1,
				StringUtils.EMPTY, StringUtils.EMPTY);
		var object = result.data().last().get("result");
		assertThat(object).isInstanceOf(Forecast.class);
	}

	@Test()
	public void testGetForecastByGeoCode_ThrowsBpmException(BpmClient bpmClient) throws NoSuchFieldException {
		try {
			bpmClient.start().subProcess(GET_FORECAST_BY_GEOCODE).execute(null, null, 1, StringUtils.EMPTY,
					StringUtils.EMPTY);
		} catch (BpmError e) {
			assertThat(e.getHttpStatusCode()).isEqualTo(400);
		}
	}
}
