package ganadinote.main.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ganadinote.common.domain.Pet;
import ganadinote.main.mapper.MainMapper;
import ganadinote.main.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class MainServiceImpl implements MainService{
	
	private final MainMapper mainMapper;
	private static final String DEFAULT_IMAGE = "/assets/images/samples/dogdefault.png";
	
	@Override
	public List<Pet> getPetInfoByMbrCd(String mbrCd) {
		System.out.println("MainServiceImpl: 펫정보 조회 시작");
		
		List<Pet> petList = mainMapper.getPetInfoByMbrCd(mbrCd);
		System.out.println("MainServiceImpl : 펫 조회 완료, 총" + petList.size()+"마리");
		
		for (Pet p : petList) {
			if(p.getPetProfileImgUrl() == null || p.getPetProfileImgUrl().isEmpty()) {
				p.setPetProfileImgUrl(DEFAULT_IMAGE);
			}
		}
		
		return petList;
	}

}
