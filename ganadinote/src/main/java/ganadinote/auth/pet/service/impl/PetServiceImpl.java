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

    @Override
    @Transactional
    public boolean registerPet(PetDTO petDTO, MultipartFile petProfileImg, FileUtils fileUtils) {
        
        if (petProfileImg != null && !petProfileImg.isEmpty()) {
            // FileUtils를 사용해 파일을 업로드하고 FileMetaData를 받습니다.
            FileMetaData fileMetaData = fileUtils.uploadFile(petProfileImg, "pet_profiles");

            // 업로드된 파일의 상대 경로를 DTO에 설정합니다.
            if (fileMetaData != null) {
                petDTO.setPetProfileImgUrl(fileMetaData.getFilePath());
            }
        }
        
        // 반려견 정보 DB에 삽입
        int result = petMapper.insertPet(petDTO);
        
        return result > 0;
    }
}