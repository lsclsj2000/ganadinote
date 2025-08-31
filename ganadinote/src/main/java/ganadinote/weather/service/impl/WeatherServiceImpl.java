package ganadinote.weather.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
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
		private final ObjectMapper objectMapper;
		
		public WeatherServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
			this.restTemplate = restTemplate;
			this.objectMapper = objectMapper;
		}
		
		@Override
		public WeatherInfo getWeather(double lat, double lon) {
			String url = String.format(
		            "https://api.openweathermap.org/data/3.0/onecall?lat=%f&lon=%f&exclude=minutely,alerts&appid=%s&units=metric&lang=kr",
		            lat, lon, apiKey
		        );
			
			 try {
			 	log.info("OpenWeatherMap API 호출 URL: {}", url);
				// ⭐⭐⭐ [수정] API를 한 번만 호출하여 String으로 응답 받기
				String response = restTemplate.getForObject(url, String.class);
				log.info("OpenWeatherMap API 응답: {}", "ok" /* response */);
				
				// ⭐⭐⭐ [수정] 받은 응답을 ObjectMapper로 객체 변환하여 반환
				return objectMapper.readValue(response, WeatherInfo.class);
			}catch(HttpClientErrorException e) {
				log.error("날씨 API 호출 중 클라이언트 오류 발생: HTTP 상태 코드 = {}", e.getStatusCode(), e);
				log.error("오류 응답 본문: {}", e.getResponseBodyAsString()); 
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
			            // ⭐⭐⭐ [수정] 올바른 엔드포인트로 변경
			            "http://api.openweathermap.org/data/2.5/air_pollution/forecast?lat=%f&lon=%f&appid=%s",
			            lat, lon, apiKey
			        );
			 
			 try {
				 log.info("Air Pollution API 호출 URL: {}", url);
				 // ⭐⭐⭐ [수정] API를 한 번만 호출하여 String으로 응답 받기
				 String response = restTemplate.getForObject(url,  String.class);
				 log.info("Air Pollution API 응답: {}","ok"/*response*/);
				 
				 // ⭐⭐⭐ [수정] 받은 응답을 ObjectMapper로 객체 변환하여 반환
				 return objectMapper.readValue(response, AirPollutionDTO.class);
			 }catch (HttpClientErrorException e) {
				 log.error("대기질 API 호출 중 클라이언트 오류 발생: HTTP 상태 코드 = {}", e.getStatusCode(), e);
				 log.error("오류 응답 본문: {}", e.getResponseBodyAsString());
				 return null;
			 }catch(ResourceAccessException e) {
				 log.error("대기질 API서버에 연결할 수 없습니다.", e);
				 return null;
			 }catch(Exception e) {
				 log.error("대기질 API 호출 중 예상치 못한 오류 발생",e);
				 return null;
			 }
		}
}