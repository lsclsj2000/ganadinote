package ganadinote.location.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ganadinote.common.util.TokenUtils;
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
	
	private Integer getMbrCdFromToken() {
        String mbrCdStr = TokenUtils.getMbrCd();
        if (mbrCdStr == null || mbrCdStr.trim().isEmpty()) {
            return null; // 토큰에 회원 코드가 없으면 null 반환
        }
        try {
            return Integer.parseInt(mbrCdStr);
        } catch (NumberFormatException e) {
            log.error("회원 코드(mbrCd)를 숫자로 변환하는 데 실패했습니다.", e);
            return null;
        }
    }
	
	@PostMapping("/location")
	public LocationResponseDTO receiveLocation(@RequestBody LocationDTO locationDTO) {
		
		LocationResponseDTO responseDTO = new LocationResponseDTO();
		
		try{
			// ⭐️⭐️⭐️ [수정] 토큰에서 회원 코드 가져오기
			Integer mbrCd = getMbrCdFromToken();
            if (mbrCd == null) {
                responseDTO.setLocationName(null);
                responseDTO.setError("인증되지 않은 사용자입니다.");
                return responseDTO;
            }
            
			String locationName = locationService.processLocation(locationDTO.getLatitude(),locationDTO.getLongitude());
			
			locationService.updateMemberLocation(mbrCd, locationDTO.getLatitude(), locationDTO.getLongitude());
			
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
			// ⭐️⭐️⭐️ [수정] 토큰에서 회원 코드 가져오기
            Integer mbrCd = getMbrCdFromToken();
            if (mbrCd == null) {
                responseDTO.setLocationName(null);
                responseDTO.setError("인증되지 않은 사용자입니다.");
                return responseDTO;
            }
			
			String ipAddress = request.getHeader("X-Forwarded-For");
			if(ipAddress == null || ipAddress.isEmpty()) {
				ipAddress = request.getRemoteAddr();
			}
			
			log.info("IP 주소로 위치 정보 처리 시작: {}", ipAddress);
			
			LocationUpdateDTO ipLocationDto = ipLocationService.getLocationFromIp(ipAddress);
			
			if (ipLocationDto != null) {
				locationService.updateMemberLocation(mbrCd, ipLocationDto.getLatitude(), ipLocationDto.getLongitude());
				String locationName = locationService.processLocation(ipLocationDto.getLatitude(), ipLocationDto.getLongitude());
				
				responseDTO.setLocationName(locationName);
				responseDTO.setError(null);
				responseDTO.setLatitude(ipLocationDto.getLatitude());
				responseDTO.setLongitude(ipLocationDto.getLongitude());
				
			} else {
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