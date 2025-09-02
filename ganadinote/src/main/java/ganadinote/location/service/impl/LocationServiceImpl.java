package ganadinote.location.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ganadinote.location.domain.LocationDTO;
import ganadinote.location.mapper.LocationMapper;
import ganadinote.location.service.LocationService;
import ganadinote.location.service.ReverseGeocodingService;
import ganadinote.notification.domain.LocationUpdateDTO;
import ganadinote.notification.mapper.PushMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class LocationServiceImpl implements LocationService {
	
	private final PushMapper pushMapper;
	private final LocationMapper locationMapper;
	
	@Autowired
	private ReverseGeocodingService reverseGeocodingService;
	
	 @Override
	    public String processLocation(double latitude, double longitude, Integer mbrCd) {
	        log.info("LocationServiceImpl: 위치정보처리 시작");

	        // 1. 위도, 경도로 주소 변환
	        String locationName = reverseGeocodingService.getLocationName(longitude, latitude);

	        if (locationName == null || locationName.isEmpty()) {
	            locationName = "위치 정보를 찾을 수 없습니다.";
	        }

	        // 2. ⭐⭐ 핵심 수정 부분: mbrCd가 null이 아닐 때만 DB에 위치 정보 업데이트
	        if (mbrCd != null) {
	            this.updateMemberLocation(mbrCd, latitude, longitude);
	            log.info("LocationServiceImpl: 회원({}) 위치 정보 DB에 업데이트 완료", mbrCd);
	        } else {
	            log.info("LocationServiceImpl: 게스트이므로 위치 정보 DB에 저장하지 않습니다.");
	        }

	        log.info("LocationServiceImpl: 현재 위치는 {}입니다", locationName);
	        log.info("LocationServiceImpl: 위치 정보 처리 완료");

	        return locationName;
	    }
	
	@Override
	public void updateMemberLocation(Integer mbrCd, Double latitude, Double longitude) {
		 LocationUpdateDTO dto = new LocationUpdateDTO();
	        dto.setMbrCd(mbrCd);
	        dto.setLatitude(latitude);
	        dto.setLongitude(longitude);
	        pushMapper.updateLocation(dto);
	    }
	
	// 회원 코드로 위치 정보 조회
	@Override
	public LocationDTO getMemberLocation(Integer mbrCd) {
		return locationMapper.getMemberLocation(mbrCd);
	}
}
