package ganadinote.location.service;

public interface LocationService {

	String processLocation(double latitude, double longitude);
	
	// 회원의 위치 정보(위도, 경도)를 업데이트합니다.
	void updateMemberLocation(Integer mbrCd, Double latitude, Double longitude);
}
