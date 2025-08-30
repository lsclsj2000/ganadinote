package ganadinote.location.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ganadinote.location.service.LocationService;
import ganadinote.location.service.ReverseGeocodingService;

@Service
public class LocationServiceImpl implements LocationService {
	
	@Autowired
	private ReverseGeocodingService reverseGeocodingService;
	
	@Override
	public String processLocation(double latitude, double longitude) {
		System.out.println("LocationServiceImpl : 위치정보처리 시작");
		
		// 위치 정보 가져오기
		String LocationName = reverseGeocodingService.getLocationName(longitude, latitude);
		
	if(LocationName == null || LocationName.isEmpty()) {
		LocationName = "위치 정보를 찾을 수 없습니다.";
	}
	
		System.out.println("LocationServiceImpl: 현재 위치는 " + LocationName +"입니다");
		
		System.out.println("LocationServiceImpl: 위치 정보 처리 완료");
		
		return LocationName;		
	}
}
