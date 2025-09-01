package ganadinote.location.controller;

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
import ganadinote.notification.domain.LocationUpdateDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/locationapi")
@CrossOrigin(origins = "*")
@Log4j2
public class LocationAPIController {
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private IpLocationService ipLocationService;
	
	@PostMapping("/location")
	public LocationResponseDTO receiveLocation(@RequestBody LocationDTO locationDTO) {
		
		LocationResponseDTO responseDTO = new LocationResponseDTO();
		
		try{
			String locationName = locationService.processLocation(locationDTO.getLatitude(),locationDTO.getLongitude());
			
			locationService.updateMemberLocation(1, locationDTO.getLatitude(), locationDTO.getLongitude());
			
			log.info("gps 주소로 위치 정보 처리 시작: {}", locationName);
			responseDTO.setLocationName(locationName);
			responseDTO.setError(null);
		} catch(Exception e){
			responseDTO.setLocationName(null);
			responseDTO.setError("위치 정보 처리 중 오류 발생");
			e.printStackTrace();
		}
		
		return responseDTO;
	}
	
	@GetMapping("/ipLocation")
	public LocationResponseDTO getIpLocation(HttpServletRequest request) {
		LocationResponseDTO responseDTO = new LocationResponseDTO();
		
		try {
			// ⭐⭐⭐ [수정] IP 주소 가져오는 로직만 처리
			String ipAddress = request.getHeader("X-Forwarded-For");
			if(ipAddress == null || ipAddress.isEmpty()) {
				ipAddress = request.getRemoteAddr();
			}
			
			log.info("IP 주소로 위치 정보 처리 시작: {}", ipAddress);
			
			// ⭐⭐⭐ [수정] IP 주소로 위치 정보를 가져오고 DTO 변수를 초기화
			LocationUpdateDTO ipLocationDto = ipLocationService.getLocationFromIp(ipAddress);
			
			// ⭐⭐⭐ [수정] IP 위치 정보가 성공적으로 파악되었는지 확인
			if (ipLocationDto != null) {
				locationService.updateMemberLocation(1, ipLocationDto.getLatitude(), ipLocationDto.getLongitude());
				String locationName = locationService.processLocation(ipLocationDto.getLatitude(), ipLocationDto.getLongitude());
				
				responseDTO.setLocationName(locationName);
				responseDTO.setError(null);
				responseDTO.setLatitude(ipLocationDto.getLatitude());
				responseDTO.setLongitude(ipLocationDto.getLongitude());
				
			} else {
				// ipLocationDto가 null일 경우, 즉 IP로 위치 파악에 실패한 경우
				responseDTO.setLocationName(null);
				responseDTO.setError("IP로 위치 정보 파악 실패");
			}
		} catch(Exception e) {
			responseDTO.setLocationName(null);
			responseDTO.setError("IP 위치 정보 처리 중 오류 발생");
			e.printStackTrace();
		}
		return responseDTO;
	}
}