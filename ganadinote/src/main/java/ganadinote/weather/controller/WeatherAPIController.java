package ganadinote.weather.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ganadinote.weather.domain.AirPollutionDTO;
import ganadinote.weather.domain.WeatherInfo;
import ganadinote.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*") 
@Log4j2
public class WeatherAPIController {
	
	private final WeatherService weatherService;

	// 날씨 api
	@GetMapping
    public ResponseEntity<WeatherInfo> getWeather(@RequestParam double lat, @RequestParam double lon) {
		 log.info("날씨 API 호출: 위도={}, 경도={}", lat, lon);
		 WeatherInfo weatherInfo = weatherService.getWeather(lat, lon);
		 if(weatherInfo == null) {
			 return ResponseEntity.status(500).build();
		 }
		 // Spring이 WeatherInfo 객체를 자동으로 JSON으로 변환하여 응답합니다.
		 return ResponseEntity.ok(weatherInfo);
    }
	
	// 미세먼지 api
	@GetMapping("/air-pollution")
	public ResponseEntity<AirPollutionDTO> getAirPollution(@RequestParam double lat, @RequestParam double lon) {
		log.info("대기질 API 호출: 위도={}, 경도={}", lat, lon);
		AirPollutionDTO airPollutionDTO = weatherService.getAirPollution(lat, lon);
		if(airPollutionDTO == null) {
			return ResponseEntity.status(500).build();
		}
		// Spring이 AirPollutionDTO 객체를 자동으로 JSON으로 변환하여 응답합니다.
		return ResponseEntity.ok(airPollutionDTO);
		
		}
		
	
}