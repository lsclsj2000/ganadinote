package ganadinote.location.service;

import ganadinote.notification.domain.LocationUpdateDTO;

public interface IpLocationService {

	String getLocationName(String ipAddress);
	
	// IP 주소를 위도, 경도 정보로 변환.
	LocationUpdateDTO getLocationFromIp(String ipAddress);
}
