package com.axonivy.connector.openweather.enums;

public enum TypeOfDegree {
	CELSIUS("°C"), FAHRENHEIT("°F");

	private final String symbol;

	private TypeOfDegree(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}
}
