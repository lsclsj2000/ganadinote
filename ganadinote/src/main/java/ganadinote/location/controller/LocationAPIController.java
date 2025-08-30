package ganadinote.location.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ganadinote.location.domain.LocationDTO;
import ganadinote.location.domain.LocationResponseDTO;
import ganadinote.location.service.IpLocationService;
import ganadinote.location.service.LocationService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/locationapi")
@CrossOrigin(origins = "*")
public class LocationAPIController {
	
	// gps 위치
	@Autowired
	private LocationService locationService;
	
	// ip 위치
	@Autowired
	private IpLocationService ipLocationService;
	
	// gps 위치
	@PostMapping("/location")
	public LocationResponseDTO receiveLocation(@RequestBody LocationDTO locationDTO) {
		
		LocationResponseDTO responseDTO = new LocationResponseDTO();
		
		try{
			String locationName = locationService.processLocation(locationDTO.getLatitude(),locationDTO.getLongitude());
			System.out.println("gps 주소로 위치 정보 처리 시작: " +  locationName);
			responseDTO.setLocationName(locationName);
			responseDTO.setError(null);
		} catch(Exception e){
			responseDTO.setLocationName(null);
			responseDTO.setError("위치 정보 처리 중 오류 발생");
		}
		
		return responseDTO;
	}
	// ip 위치
	@GetMapping("/ipLocation")
	public LocationResponseDTO getIpLocation(HttpServletRequest request) {
			LocationResponseDTO responseDTO = new LocationResponseDTO();
			
			try {
				// 클라이언트의 실제 IP 주소 가져오기
				String ipAddress = request.getHeader("X-Forwarded-For");
				if(ipAddress == null) {
					ipAddress = request.getRemoteAddr();
				}
				System.out.println("IP 주소로 위치 정보 처리 시작: " +  ipAddress);
				
				String locationName = ipLocationService.getLocationName(ipAddress);
				
				responseDTO.setLocationName(locationName);
				responseDTO.setError(null);
				
			}catch(Exception e) {
				responseDTO.setLocationName(null);
				responseDTO.setError("IP 위치 정보 처리 중 오류 발생");
				e.printStackTrace();
			}
			return responseDTO;
	}
}
