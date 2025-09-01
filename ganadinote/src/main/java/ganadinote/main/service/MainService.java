package ganadinote.main.service;

import java.util.List;

import ganadinote.common.domain.Pet;
import ganadinote.notification.domain.PetWithBreedDTO;

public interface MainService {
	
	List<Pet> getPetInfoByMbrCd(Integer mbrCd);
	
	List<PetWithBreedDTO> getPetInfoWithBreedByMbrCd(Integer mbrCd);
	
	String getNknmByMbrCd(Integer mbrCd);
}
