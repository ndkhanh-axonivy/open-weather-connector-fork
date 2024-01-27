package com.axonivy.connector.openweather.exception;

public class OpenWeatherMapException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public OpenWeatherMapException() {
		super();
	}

	public OpenWeatherMapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public OpenWeatherMapException(String message, Throwable cause) {
		super(message, cause);
	}

	public OpenWeatherMapException(String message) {
		super(message);
	}

	public OpenWeatherMapException(Throwable cause) {
		super(cause);
	}
}
