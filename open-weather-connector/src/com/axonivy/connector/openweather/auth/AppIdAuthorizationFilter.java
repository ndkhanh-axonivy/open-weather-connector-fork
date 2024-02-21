package com.axonivy.connector.openweather.auth;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.ivy.rest.client.FeatureConfig;

public class AppIdAuthorizationFilter implements ClientRequestFilter {

	public static interface Property {
		String APP_ID = "AUTH.appId";
	}

	@Override
	public void filter(ClientRequestContext ctxt) throws IOException {
		var config = new FeatureConfig(ctxt.getConfiguration(), OpenWeatherAuthFeature.class);
		UriBuilder builder = UriBuilder.fromUri(ctxt.getUri());
		builder.queryParam("appId", config.readMandatory(Property.APP_ID));
		ctxt.setUri(builder.build());
	}
}
