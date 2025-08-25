package ganadinote.location.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ganadinote.location.service.IpLocationService;

@Service
public class IpLocationServiceImpl implements IpLocationService{
	
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public String getLocationName(String ipAddress) {
		//  ip-api.com api 호출
		String url = "http://ip-api.com/json/" + ipAddress + "?lang=ko";
		
		try {
			String response = restTemplate.getForObject(url,String.class);
			JsonNode root = objectMapper.readTree(response);
			
			//api 응답에서 국가와 도시 정보 추출 
			String city = root.path("city").asText();
			String country = root.path("country").asText();
			String status = root.path("status").asText();
			
			if("success".equals(status)) {
				return city +"," + country;
			}else {
				return "알 수 없는 지역";
			}
		} catch(Exception e) {
			e.printStackTrace();
			return "정보 조회 실패";
			}			
	}
}
