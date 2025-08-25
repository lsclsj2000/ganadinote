package ganadinote.weather.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping
	public ResponseEntity<?> getWeather(@RequestParam double lat, @RequestParam double lon){
		try {
			String WeatherData = weatherService.getWeather(lat, lon);
			return ResponseEntity.ok(WeatherData);
		}catch(Exception e) {
			log.error("날씨정보를 가져오는 중 오류 발생",e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("날씨정보를 가져오는데 실패했습니다.");
		}		
	}	
}
