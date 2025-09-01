package ganadinote.location.service;

import ganadinote.location.domain.LocationDTO;

public interface LocationService {

	String processLocation(double latitude, double longitude);
	
	// 회원의 위치 정보(위도, 경도)를 업데이트합니다.
	void updateMemberLocation(Integer mbrCd, Double latitude, Double longitude);
	
	// 회원 코드로 위치 정보 조회
	LocationDTO getMemberLocation(Integer mbrCd);
	
}
