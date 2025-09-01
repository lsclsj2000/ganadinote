package ganadinote.auth.pet.service.impl;

import ganadinote.auth.pet.dto.PetDTO;
import ganadinote.auth.pet.mapper.PetMapper;
import ganadinote.auth.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetMapper petMapper;

    @Override
    @Transactional
    public boolean registerPet(PetDTO petDTO) {
        // 이미지 업로드 로직이 추가될 경우 여기에 구현
        // petDTO.setPetProfileImgUrl(...);

        // 반려견 정보 DB에 삽입
        int result = petMapper.insertPet(petDTO);
        
        return result > 0;
    }
}
