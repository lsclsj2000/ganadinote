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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/locationapi")
@CrossOrigin(origins = "*")
@Log4j2
@RequiredArgsConstructor
public class LocationAPIController {
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private IpLocationService ipLocationService;
	
	private Integer getMbrCdFromToken() {
        String mbrCdStr = TokenUtils.getMbrCd();
        if (mbrCdStr == null || mbrCdStr.trim().isEmpty()) {
            return null;
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
			// ⭐⭐ [수정] 토큰에서 회원 코드 가져오기 (null이어도 계속 진행)
			Integer mbrCd = getMbrCdFromToken();
            
            // 기존 로직: 토큰이 없으면 바로 반환하는 로직 삭제
            // 게스트일 경우 DB 업데이트만 건너뛰도록 서비스에 null 전달
            
			String locationName = locationService.processLocation(locationDTO.getLatitude(),locationDTO.getLongitude(), mbrCd);
			
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
			// ⭐⭐ [수정] 토큰에서 회원 코드 가져오기 (null이어도 계속 진행)
            Integer mbrCd = getMbrCdFromToken();

            // 기존 로직: 토큰이 없으면 바로 반환하는 로직 삭제
            // 게스트일 경우 DB 업데이트만 건너뛰도록 서비스에 null 전달
			
			String ipAddress = request.getHeader("X-Forwarded-For");
			if(ipAddress == null || ipAddress.isEmpty()) {
				ipAddress = request.getRemoteAddr();
			}
			
			log.info("IP 주소로 위치 정보 처리 시작: {}", ipAddress);
			
			LocationUpdateDTO ipLocationDto = ipLocationService.getLocationFromIp(ipAddress);
			
			if (ipLocationDto != null) {
                // ⭐⭐ [수정] updateMemberLocation 대신 통합된 processLocation 호출
				String locationName = locationService.processLocation(ipLocationDto.getLatitude(), ipLocationDto.getLongitude(), mbrCd);
				
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