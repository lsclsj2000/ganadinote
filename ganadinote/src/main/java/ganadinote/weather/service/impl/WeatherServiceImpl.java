package ganadinote.weather.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import ganadinote.weather.domain.AirPollutionDTO;
import ganadinote.weather.domain.WeatherInfo;
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
		public WeatherInfo getWeather(double lat, double lon) {
			String url = String.format(
		            "https://api.openweathermap.org/data/3.0/onecall?lat=%f&lon=%f&exclude=minutely,alerts&appid=%s&units=metric&lang=kr",
		            lat, lon, apiKey
		        );
			
			 try {
				 	// OpenWeatherMap 호출 url
					log.info("OpenWeatherMap API 호출 URL: {}", url);
					String response = restTemplate.getForObject(url, String.class);
					// 날씨 정보 로그
					// log.info("OpenWeatherMap API 응답: {}", response); 
					return restTemplate.getForObject(url, WeatherInfo.class);
				}catch(HttpClientErrorException e) {
					log.error("날씨 API 호출 중 클라이언트 오류 발생: HTTP 상태 코드 = {}", e.getStatusCode(), e);
					log.error("오류 응답 본문: {}", e.getResponseBodyAsString()); // ⚠️ 추가된 디버깅 라인
					return null;
				}catch(ResourceAccessException e) {
					log.error("날씨 API서버에 연결할 수 없습니다.", e);
					return null;
				}catch(Exception e) {
					log.error("날씨 API 호출 중 예상치 못한 오류 발생",e);
					return null;
				}				
	}
		
		@Override
		public AirPollutionDTO getAirPollution(double lat, double lon) {
			 String url = String.format(
			            "http://api.openweathermap.org/data/2.5/air_pollution?lat=%f&lon=%f&appid=%s",
			            lat, lon, apiKey
			        );
			 
			 try {
				 log.info("Air Pollution API 호출 URL: {}", url);
				 String response = restTemplate.getForObject(url,  String.class);
				 log.info("Sir Pollution API 응답: {}",response);
				 return restTemplate.getForObject(url, AirPollutionDTO.class);
			 }catch (HttpClientErrorException e) {
				 log.error("대기질 API 호출 중 클라이언트 오류 발생: HTTP 상태 코드 = {}", e.getStatusCode(), e);
		         log.error("오류 응답 본문: {}", e.getResponseBodyAsString());
		         return null;
			 }catch(ResourceAccessException  e) {
				 log.error("대기질 API 서벌에 연결할 수 없습니다.", e);
				 return null;
			 }catch(Exception e) {
				 log.error("대기질 API 호출 중 예상치 못한 오류 발생", e);
				 return null;
			 }
		}
}
