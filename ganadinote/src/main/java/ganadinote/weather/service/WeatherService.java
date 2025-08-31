package ganadinote.weather.service;

import ganadinote.weather.domain.AirPollutionDTO;
import ganadinote.weather.domain.WeatherInfo;

public interface WeatherService {
	WeatherInfo getWeather(double lat, double lon);
	
	AirPollutionDTO getAirPollution(double lat, double lon);
	
}
