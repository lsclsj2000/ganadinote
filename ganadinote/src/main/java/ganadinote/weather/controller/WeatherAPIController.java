package ganadinote.weather.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

	// 날씨 api
	@GetMapping
    public ResponseEntity<String> getWeather(@RequestParam double lat, @RequestParam double lon) {
        try {
            // WeatherService로부터 받은 JSON 문자열을 그대로 사용합니다.
            String weatherData = weatherService.getWeather(lat, lon);

            // 응답 헤더에 이 데이터가 JSON임을 명시해줍니다.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // JSON 문자열을 그대로 응답 본문에 담아 성공(OK) 상태로 반환합니다.
            return new ResponseEntity<>(weatherData, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("날씨정보를 가져오는 중 오류 발생", e);
            // 오류 발생 시, 클라이언트가 이해할 수 있는 JSON 형태의 오류 메시지를 반환하는 것이 좋습니다.
            String errorMessage = "{\"error\":\"날씨정보를 가져오는데 실패했습니다.\"}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(errorMessage, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GetMapping("/air-pollution")
	public ResponseEntity<String> getAirPollution(@RequestParam double lat, @RequestParam double lon) {
		try {
			// air-pollution api 호출 로직을 WeatherService에 추가 및 호출
			String airPollutionData = weatherService.getAirPollution(lat, lon);
			
			HttpHeaders headers =new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			return new ResponseEntity<>(airPollutionData, headers, HttpStatus.OK);
					
		}catch(Exception e) {
			log.error("대기질 정보를 가져오는 중 오류 발생",e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"대기질 정보를 가져오는데 실패했습니다.\"}");
			
		}
		
	}
}