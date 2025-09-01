package ganadinote.main.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ganadinote.common.domain.Pet;
import ganadinote.common.util.TokenUtils;
import ganadinote.location.domain.LocationDTO;
import ganadinote.location.service.LocationService;
import ganadinote.main.service.MainService;
import ganadinote.weather.domain.AirPollutionDTO;
import ganadinote.weather.domain.WeatherInfo;
import ganadinote.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {
	
	private final MainService mainService;
	private final WeatherService weatherService; 
	private final LocationService locationService;
	
	@GetMapping("/weather")
	public String getPetInfoByMbrCd(Model model) {
		String mbrCdStr = TokenUtils.getMbrCd();
		Integer mbrCd = null;
		String mbrNknm = "게스트"; 
		List<Pet> petList = new ArrayList<>();
		 log.info("로그인 토큰에서 가져온 회원 코드(mbrCdStr): " + mbrCdStr);

		// 1. 회원 정보 로직
		if (mbrCdStr != null && !mbrCdStr.trim().isEmpty()) {
			try {
				mbrCd = Integer.parseInt(mbrCdStr);
				
				String foundNknm = mainService.getNknmByMbrCd(mbrCd);
				log.info("서비스에서 조회된 닉네임(foundNknm): " + foundNknm);
				
				log.info("조회된 닉네임: " + foundNknm);
				if (foundNknm != null) {
					mbrNknm = foundNknm;
				}
				
				// ⭐ 추가: 닉네임 정보를 모델에 담아 뷰로 전달합니다.
				model.addAttribute("mbrNknm", mbrNknm);
				
				// ✅ 수정: 펫 정보는 서비스로 별도 조회
				petList = mainService.getPetInfoByMbrCd(mbrCd);
				
			} catch (NumberFormatException e) {
				log.error("회원 코드(mbrCd)를 숫자로 변환하는 데 실패했습니다.", e);
			}
		}

		model.addAttribute("pets", petList);
		
		// 2. 위치 및 날씨/미세먼지 정보 로직
		LocationDTO location = null;
		if (mbrCd != null) {
			try {
				location = locationService.getMemberLocation(mbrCd);
			} catch (Exception e) {
				log.error("회원 위치 정보를 가져오는 데 실패했습니다.", e);
			}
		}
		
		if (location == null) {
			location = new LocationDTO();
			location.setLatitude(37.5665);
			location.setLongitude(126.9780);
			location.setLocationName("서울");
		}
		
		try {
			WeatherInfo weatherData = weatherService.getWeather(location.getLatitude(), location.getLongitude());
			AirPollutionDTO airPollutionData = weatherService.getAirPollution(location.getLatitude(), location.getLongitude());

			model.addAttribute("entireLocation", "현재 위치: '" + location.getLocationName() + "'");
			model.addAttribute("weather", weatherData);
			model.addAttribute("airPollution", airPollutionData);

		} catch (Exception e) {
			log.error("날씨/미세먼지 정보를 가져오는 데 실패했습니다.", e);
			model.addAttribute("weatherFetchError", "날씨 정보를 불러오는 데 실패했습니다.");
		}
		
		return "weather/weatherView";
	}
}