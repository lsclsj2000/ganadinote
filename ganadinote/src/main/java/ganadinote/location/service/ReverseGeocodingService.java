package ganadinote.location.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReverseGeocodingService {

	@Value("${kakao.api.key}")
	private String KAKAO_API_KEY;
	
	
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public ReverseGeocodingService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public String getLocationName(double longitude, double latitude) {
		String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json"
		+ "?x=" + longitude + "&y=" + latitude + "&input_coord=WGS84";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		
		try {
			String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
			System.out.println("카카오 API로부터 받은 JSON 응답: " + response);
			JsonNode root = objectMapper.readTree(response);
			JsonNode addressList = root.path("documents");
			
			if(addressList.isArray() && addressList.size() > 0) {
				JsonNode firstAddress = addressList.get(0);
				JsonNode roadAddress = firstAddress.path("road_address");
				JsonNode address = firstAddress.path("address");
				
				// 1. 전체 도로명 주소를 우선적으로 시도
				if(!roadAddress.isMissingNode()) {
					String roadAddressName = roadAddress.path("address_name").asText("");
					if(!roadAddressName.isEmpty()) {
						return roadAddressName;
					}
				}
				
				// 2. 전체 지번 주소로 대체 시도
				if (!address.isMissingNode()){
					String jibeonAddressName = address.path("address_name").asText("");
					if(!jibeonAddressName.isEmpty()) {
						return jibeonAddressName;
					}
				}
				
				// 3. 마지막 대안으로 '동' 이름만 반환 (이전 방식)
				if(!roadAddress.isMissingNode() && !roadAddress.path("region_3depth_name").isMissingNode()) {
					return roadAddress.path("region_3depth_name").asText();
				} else if(!address.isMissingNode() && !address.path("region_3depth_name").isMissingNode()) {
					return address.path("region_3depth_name").asText();
				}
				
			}
			
		} catch (HttpClientErrorException e) {
			System.err.println("카카오 API 호출 오류: " + e.getStatusCode() + ", " + e.getResponseBodyAsString());
		} catch (JsonProcessingException e) {
			System.err.println("JSON 파싱 오류: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("알 수 없는 오류: " + e.getMessage());
		}
		return "알 수 없는지역";
	}
}