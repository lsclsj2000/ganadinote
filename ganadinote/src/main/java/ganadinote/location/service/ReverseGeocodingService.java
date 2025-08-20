package ganadinote.location.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReverseGeocodingService {

	@Value("${kakao.api.key}")
	private String KAKAO_API_KEY; 
	
	
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public String getLocationName(double longitude, double latitude) {
		String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json"
                + "?x=" + longitude + "&y=" + latitude + "&input_coord=WGS84";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		
		try {
			// 카카오맵 API 호출
			String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
			
			//JSON 파싱
			JsonNode root = objectMapper.readTree(response);
			JsonNode addressList = root.path("documents");
			
			if(addressList.isArray() && addressList.size() > 0) {
				JsonNode firstAddress = addressList.get(0);
				JsonNode roadAddress = firstAddress.path("road_address");
				JsonNode address = firstAddress.path("address");
				
				if(!roadAddress.isMissingNode() && !roadAddress.path("region_3depth_name").isMissingNode()) {
					// 도로명 주소에서 '동' 정보 추출
					return roadAddress.path("region_3depth_name").asText();
				}else if(!address.isMissingNode() && !address.path("region_3depth_name").isMissingNode()) {
					// 지번 주소에서 '동' 정보 추출
					return address.path("region_3depth_name").asText();
				}
				
			}
			
		}catch (Exception e) {
			System.err.println("역 지오코딩 오류: " + e.getMessage());
		}
			return "알 수 없는 지역";
		
	}
}
