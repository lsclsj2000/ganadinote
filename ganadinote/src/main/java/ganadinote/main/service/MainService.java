package ganadinote.main.service;

import java.util.List;

import ganadinote.common.domain.Pet;

public interface MainService {
	
	List<Pet> getPetInfoByMbrCd(String mbrCd);
}
