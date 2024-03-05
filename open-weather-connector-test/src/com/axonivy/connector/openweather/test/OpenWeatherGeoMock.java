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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import io.swagger.v3.oas.annotations.Hidden;

@Path("weatherGeoMock")
@PermitAll
@Hidden
public class OpenWeatherGeoMock {
	@GET
	@Path("direct")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGeoByCoordinates(@QueryParam("q") String q, @QueryParam("limit") int limit) {
		if (StringUtils.isBlank(q)) {
			return Response.status(400).entity(load("json/geo-nothing.json")).build();
		}
		return Response.status(200).entity(load("json/geo-direct-result.json")).build();
	}

	@GET
	@Path("zip")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGeoByZip(@QueryParam("zip") String zip) {
		if (StringUtils.isBlank(zip)) {
			return Response.status(400).entity(load("json/geo-nothing.json")).build();
		}
		return Response.status(200).entity(load("json/geo-zip-result.json")).build();
	}

	@GET
	@Path("reverse")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLocationInformation(@QueryParam("lat") double lat, @QueryParam("lon") double lon,
			@QueryParam("limit") int limit) {
		if (ObjectUtils.anyNull(lat, lon)) {
			return Response.status(400).entity(load("json/geo-nothing.json")).build();
		}
		return Response.status(200).entity(load("json/geo-reverse-result.json")).build();
	}

	private static String load(String path) {
		try (InputStream is = OpenWeatherGeoMock.class.getResourceAsStream(path)) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to read resource: " + path);
		}
	}
}
