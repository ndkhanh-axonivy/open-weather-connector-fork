package com.axonivy.connector.openweather.util;

public class TemperatureUtilities {
	public static double celsiusToFahrenheit(double celsius) {
		return (celsius * 9 / 5) + 32;
	}

	public static double fahrenheitToCelsius(double fahrenheit) {
		return (fahrenheit - 32) * 5 / 9;
	}
}
