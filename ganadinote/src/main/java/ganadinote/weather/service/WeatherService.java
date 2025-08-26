package ganadinote.weather.service;

public interface WeatherService {
	String getWeather(double lat, double lon);
	
	String getAirPollution(double lat, double lon);
	
}
