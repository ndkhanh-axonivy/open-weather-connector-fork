package com.axonivy.connector.openweather.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openweathermap.api.data2_5.client.Current;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.error.BpmError;

public class CurrentWeatherProcessTest extends BaseProcessTest {
	private static final BpmProcess GET_CURRENT_WEATHER_PROCESS = BpmProcess.path("connector/CurrentWeather");
	private static final BpmElement GET_CURRENT_WEATHER_BY_GEOCODE = GET_CURRENT_WEATHER_PROCESS
			.elementName("getCurrentWeather(Double,Double,String,String)");

	@Test
	public void testGetCurrentWeatherByGeoCode_ReturnsCurrentWeather(BpmClient bpmClient)
			throws NoSuchFieldException {
		ExecutionResult result = bpmClient.start().subProcess(GET_CURRENT_WEATHER_BY_GEOCODE).execute(40.7127281,
				-74.0060152, StringUtils.EMPTY, StringUtils.EMPTY);
		var object = result.data().last().get("result");
		assertThat(object).isInstanceOf(Current.class);
	}

	@Test()
	public void testGetCurrentWeatherByGeoCode_ThrowsBpmException(BpmClient bpmClient) throws NoSuchFieldException {
		try {
			bpmClient.start().subProcess(GET_CURRENT_WEATHER_BY_GEOCODE).execute(null, null, StringUtils.EMPTY,
					StringUtils.EMPTY);
		} catch (BpmError e) {
			assertThat(e.getHttpStatusCode()).isEqualTo(400);
		}
	}
}
