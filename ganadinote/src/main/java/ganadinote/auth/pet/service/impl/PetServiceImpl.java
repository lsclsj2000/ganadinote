package ganadinote.auth.pet.service.impl;

import ganadinote.auth.pet.dto.PetDTO;
import ganadinote.auth.pet.mapper.PetMapper;
import ganadinote.auth.pet.service.PetService;
import ganadinote.common.domain.FileMetaData;
import ganadinote.common.file.FileUtils; // FileUtils import
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetServiceImpl implements PetService {

    private final PetMapper petMapper;
    private final FileUtils fileUtils;

    @Override
    @Transactional
    public boolean registerPet(PetDTO petDTO, MultipartFile petProfileImg) {
        
        if (petProfileImg != null && !petProfileImg.isEmpty()) {
            // FileUtils를 사용해 파일을 업로드하고 FileMetaData를 받습니다.
            FileMetaData fileMetaData = fileUtils.uploadFile(petProfileImg, "pet_profiles");

            // 업로드된 파일의 상대 경로를 DTO에 설정합니다.
            if (fileMetaData != null) {
                petDTO.setPetProfileImgUrl(fileMetaData.getFilePath());
            }
        }
        
    	// 1. 반려견 정보 DB에 삽입 (이때 pet_cd가 petDTO 객체에 설정됨)
        int petResult = petMapper.insertPet(petDTO);
        if (petResult <= 0) {
            return false;
        }

        // 2. profile_card 테이블에 데이터 삽입
        int cardResult = petMapper.insertProfileCard(petDTO);
        
        return cardResult > 0;
    }
}