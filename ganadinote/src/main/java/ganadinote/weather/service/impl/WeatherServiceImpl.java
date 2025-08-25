package ganadinote.weather.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import ganadinote.weather.service.WeatherService;
import lombok.extern.log4j.Log4j2;


@Service
@Log4j2
public class WeatherServiceImpl implements WeatherService{
		
		@Value("${openweathermap.api.key}")
		private String apiKey;
		
		private final RestTemplate restTemplate;
		
		public WeatherServiceImpl(RestTemplate restTemplate) {
			this.restTemplate = restTemplate;
		}
		
		@Override
		public String getWeather(double lat, double lon) {
			String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric&lang=kr",
		            lat, lon, apiKey);
			
			try {
				return restTemplate.getForObject(url, String.class);
			}catch(HttpClientErrorException e) {
				log.error("날씨 API 호출 중 클라이언트 오류 발생: HTTP 상태 코드 = {}", e.getStatusCode(), e);
				return null;
			}catch(ResourceAccessException e) {
				log.error("날씨 API서버에 연결할 수 없습니다.", e);
				return null;
			}catch(Exception e) {
				log.error("날씨 API 호출 중 예상치 못한 오류 발생",e);
				return null;
			}						
	}
}
