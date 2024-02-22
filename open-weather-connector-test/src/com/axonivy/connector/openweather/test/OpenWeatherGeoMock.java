package com.axonivy.connector.openweather.test;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;

import io.swagger.v3.oas.annotations.Hidden;

@Path("weatherGeoMock")
@PermitAll
@Hidden
public class OpenWeatherGeoMock {

}
