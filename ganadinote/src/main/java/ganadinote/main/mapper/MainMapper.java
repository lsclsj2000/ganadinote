package ganadinote.main.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import ganadinote.common.domain.Pet;

@Mapper
public interface MainMapper {
	
	List<Pet> getPetInfoByMbrCd(String mbrCd);
	
}
