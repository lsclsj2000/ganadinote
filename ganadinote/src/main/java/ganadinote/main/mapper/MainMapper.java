package ganadinote.main.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ganadinote.common.domain.Pet;
import ganadinote.notification.domain.PetWithBreedDTO;

@Mapper
public interface MainMapper {
	
	List<Pet> getPetInfoByMbrCd(String mbrCd);
	
    List<PetWithBreedDTO> getPetInfoWithBreedByMbrCd(@Param("mbrCd") String mbrCd);
	
}
