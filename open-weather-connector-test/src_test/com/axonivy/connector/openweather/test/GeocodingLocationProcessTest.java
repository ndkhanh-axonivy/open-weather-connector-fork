package com.axonivy.connector.openweather.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openweathermap.api.geo1_0.client.GeoLocation;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.error.BpmError;

public class GeocodingLocationProcessTest extends BaseProcessTest {

	private static final BpmProcess GEOCODING_LOCATION_PROCESS = BpmProcess.path("connector/GeocodingLocation");
	private static final BpmElement GEOCODING_LOCATION_BY_NAME = GEOCODING_LOCATION_PROCESS
			.elementName("getCoordinatesByLocationName(String,String,String,Integer)");
	private static final BpmElement GEOCODING_LOCATION_BY_ZIP_CODE = GEOCODING_LOCATION_PROCESS
			.elementName("getCoordinatesByZipCode(String,String)");
	private static final BpmElement GEOCODING_LOCATION_REVERSE = GEOCODING_LOCATION_PROCESS
			.elementName("reverse(Double,Double,Integer)");

	@Test
	public void testGeocodingByName_ReturnsListOfGeoLocations(BpmClient bpmClient) throws NoSuchFieldException {
		ExecutionResult result = bpmClient.start().subProcess(GEOCODING_LOCATION_BY_NAME).execute("New York",
				StringUtils.EMPTY, StringUtils.EMPTY, 1);
		var object = result.data().last().get("results");
		assertThat(object).isInstanceOf(List.class);
		var objects = (ArrayList<?>) object;
		assertThat(objects).isNotEmpty();
		assertThat(objects.get(0)).isInstanceOf(GeoLocation.class);
	}

	@Test()
	public void testGeocodingByName_ThrowsBpmException(BpmClient bpmClient) throws NoSuchFieldException {
		try {
			bpmClient.start().subProcess(GEOCODING_LOCATION_BY_NAME).execute(StringUtils.EMPTY, StringUtils.EMPTY,
					StringUtils.EMPTY, 1);
		} catch (BpmError e) {
			assertThat(e.getHttpStatusCode()).isEqualTo(400);
		}
	}

	@Test
	public void testGeocodingByZip_ReturnsGeoLocation(BpmClient bpmClient) throws NoSuchFieldException {
		ExecutionResult result = bpmClient.start().subProcess(GEOCODING_LOCATION_BY_ZIP_CODE).execute("10001",
				StringUtils.EMPTY);
		var object = result.data().last().get("result");
		assertThat(object).isInstanceOf(GeoLocation.class);
	}

	@Test()
	public void testGeocodingByZip_ThrowsBpmException(BpmClient bpmClient) throws NoSuchFieldException {
		try {
			bpmClient.start().subProcess(GEOCODING_LOCATION_BY_ZIP_CODE).execute(StringUtils.EMPTY, StringUtils.EMPTY);
		} catch (BpmError e) {
			assertThat(e.getHttpStatusCode()).isEqualTo(400);
		}
	}

	@Test
	public void testReverse_ReturnsListOfGeoLocations(BpmClient bpmClient) throws NoSuchFieldException {
		ExecutionResult result = bpmClient.start().subProcess(GEOCODING_LOCATION_REVERSE).execute(40.7484, -73.9967, 1);
		var object = result.data().last().get("results");
		assertThat(object).isInstanceOf(List.class);
		var objects = (ArrayList<?>) object;
		assertThat(objects).isNotEmpty();
		assertThat(objects.get(0)).isInstanceOf(GeoLocation.class);
	}

	@Test()
	public void testReverse_ThrowsBpmException(BpmClient bpmClient) throws NoSuchFieldException {
		try {
			bpmClient.start().subProcess(GEOCODING_LOCATION_REVERSE).execute(null, null, 1);
		} catch (BpmError e) {
			assertThat(e.getHttpStatusCode()).isEqualTo(400);
		}
	}

}
