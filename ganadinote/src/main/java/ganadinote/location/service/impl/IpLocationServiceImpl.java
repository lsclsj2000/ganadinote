package ganadinote.location.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ganadinote.location.service.IpLocationService;
import ganadinote.notification.domain.LocationUpdateDTO;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class IpLocationServiceImpl implements IpLocationService{
	
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private JsonNode getIpApiData(String ipAddress) {
		log.info("getIpApiData 메서드 시작: IP 주소 {}", ipAddress);
        String url = "http://ip-api.com/json/" + ipAddress + "?lang=ko";
        try {
            String response = restTemplate.getForObject(url, String.class);
            return objectMapper.readTree(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	@Override
	public String getLocationName(String ipAddress) {
		log.info("getLocationName 메서드 시작: IP 주소 {}", ipAddress);
		JsonNode root = getIpApiData(ipAddress);
		
		if (root != null) {
			String city = root.path("city").asText();
			String country = root.path("country").asText();
			String status = root.path("status").asText();
			
			if("success".equals(status)) {
				return city +"," + country;
			}
		}
		return "알 수 없는 지역";
	}
	
	@Override
	public LocationUpdateDTO getLocationFromIp(String ipAddress) {
		log.info("getLocationFromIp 메서드 시작: IP 주소 {}", ipAddress);
		JsonNode root = getIpApiData(ipAddress);
		
		if (root != null) {
			String status = root.path("status").asText();
			if ("success".equals(status)) {
				double latitude = root.path("lat").asDouble();
				double longitude = root.path("lon").asDouble();
				
				LocationUpdateDTO dto = new LocationUpdateDTO();
				dto.setLatitude(latitude);
				dto.setLongitude(longitude);
				
				return dto;
			}
		}
		return null;
	}
	
}