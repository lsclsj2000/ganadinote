package ganadinote.main.service;

import java.util.List;

import ganadinote.common.domain.Pet;
import ganadinote.notification.domain.PetWithBreedDTO;

public interface MainService {
	
	List<Pet> getPetInfoByMbrCd(String mbrCd);
	
	List<PetWithBreedDTO> getPetInfoWithBreedByMbrCd(String mbrCd);
}
