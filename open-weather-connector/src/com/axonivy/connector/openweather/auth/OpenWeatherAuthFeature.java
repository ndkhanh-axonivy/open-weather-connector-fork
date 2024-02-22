package com.axonivy.connector.openweather.auth;

import javax.ws.rs.Priorities;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class OpenWeatherAuthFeature implements Feature {
	
	@Override
	public boolean configure(FeatureContext context) {
		context.register(new AppIdAuthorizationFilter(), Priorities.AUTHENTICATION);
		return true;
	}
}
