package com.axonivy.connector.openweather.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import io.swagger.v3.oas.annotations.Hidden;

@Path("weatherDataMock")
@PermitAll
@Hidden
public class OpenWeatherDataMock {
	@GET
	@Path("air_pollution")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAirPollution(@QueryParam("lat") double lat, @QueryParam("lon") double lon) {
		return Response.status(200).entity(load("json/oppLists.json")).build();
	}

	@GET
	@Path("weather")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWeather(@QueryParam("lat") Double lat, @QueryParam("lon") Double lon,
			@QueryParam("lang") String lang, @QueryParam("units") String units) {
		return Response.status(200).entity(load("json/account.json")).build();
	}

	@GET
	@Path("forecast")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getForecast(@QueryParam("lat") double lat, @QueryParam("lon") double lon,
			@QueryParam("lang") String lang, @QueryParam("units") String units, @QueryParam("cnt") int cnt) {
		return Response.status(200).entity(load("json/opportunity.json")).build();
	}

	private static String load(String path) {
		try (InputStream is = OpenWeatherDataMock.class.getResourceAsStream(path)) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to read resource: " + path);
		}
	}
}
