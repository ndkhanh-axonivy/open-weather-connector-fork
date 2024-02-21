package com.axonivy.connector.openweather.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;

@IvyProcessTest
public class GeocodingLocationProcessTest {

	private static final BpmProcess GEOCODING_LOCATION_PROCESS = BpmProcess.path("connector/GeocodingLocation");
	private static final BpmElement GEOCODING_LOCATION_START = GEOCODING_LOCATION_PROCESS
			.elementName("getCoordinatesByLocationName(String,String,String,Integer)");

	@Test
	public void callProcess(BpmClient bpmClient) {
		ExecutionResult result = bpmClient.start().subProcess(GEOCODING_LOCATION_START).execute();
//		CompositeObject data = result.data().last();
//		assertThat(data);
	}

}
