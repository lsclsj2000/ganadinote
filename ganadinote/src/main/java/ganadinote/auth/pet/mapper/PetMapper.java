package ganadinote.auth.pet.mapper;

import ganadinote.auth.pet.dto.PetDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PetMapper {
    int insertPet(PetDTO petDTO);
    
    int insertProfileCard(PetDTO petDTO);
}