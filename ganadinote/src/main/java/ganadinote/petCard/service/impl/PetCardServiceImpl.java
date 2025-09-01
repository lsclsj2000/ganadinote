package ganadinote.petCard.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.FileMetaData;
import ganadinote.common.domain.PetCard;
import ganadinote.common.domain.Tag;
import ganadinote.common.file.FileMapper;
import ganadinote.common.file.FileUtils;
import ganadinote.petCard.domain.MemberHeader;
import ganadinote.petCard.mapper.PetCardMapper;
import ganadinote.petCard.service.PetCardService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PetCardServiceImpl implements PetCardService {
	
	private final PetCardMapper petCardMapper;
	private final FileUtils fileUtils;
    private final FileMapper fileMapper;
	
	// 펫 카드 - 회원 헤더
    @Override
    public MemberHeader getHeader(Integer mbrCd) {
        return petCardMapper.selectHeaderByMbrCd(mbrCd);
    }
    
	// 펫 카드
    @Override
    public List<PetCard> getPetCards(Integer mbrCd) {
        return petCardMapper.selectPetCardsByMbrCd(mbrCd);
    }
    
    // 펫 카드 수정 - 전체 태그 불러오기
    @Override
    public List<Tag> getAllTags() {
        return petCardMapper.selectAllTags();
    }

    // 펫 카드 수정 - 로그인 회원 카드
    @Override
    public boolean isOwnerOfPetCard(Integer mbrCd, Integer petId) {
        Integer owner = petCardMapper.selectOwnerMbrByCardId(petId);
        return owner != null && owner.equals(mbrCd);
    }

    // 펫 카드 수정 - 사진 저장
    @Override
    public String saveAndUpdateCardImage(Integer cardId, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) return null;

        // 1) 실제 파일 저장 → 상대 URL 리턴 (예: /attachment/pet/20250901/image/uuid.jpg)
        FileMetaData meta = fileUtils.uploadFile(imageFile, "pet");
        if (meta == null) throw new RuntimeException("파일 저장 실패");
        String newUrl = meta.getFilePath();

        // (선택) 이전 이미지 경로 조회
        String oldUrl = petCardMapper.selectPetImageUrlByCardId(cardId);

        // 2) pet 테이블 URL 갱신
        int updated = petCardMapper.updatePetImageByCardId(cardId, newUrl);

        // (선택) 성공했으면 이전 물리파일 삭제
        if (updated > 0 && oldUrl != null && !oldUrl.isBlank() && !oldUrl.equals(newUrl)) {
            fileUtils.deleteQuietly(oldUrl);
        }

        return newUrl;
    }

    // 펫 카드 수정 - 기존 내역 불러오기
    @Override
    public void updateIntroduction(Integer petId, String intro) {
        petCardMapper.updateCardIntroduction(petId, intro);
    }

    // 펫 카드 수정 - 태그 수정
    @Override
    public void replaceTagsByNames(Integer petId, List<String> tagNames) {
        // tagNames → tag_id들로 변환
        List<Integer> tagIds = petCardMapper.selectTagIdsByNames(tagNames);
        petCardMapper.deleteDogTagsByCardId(petId);
        if (tagIds != null && !tagIds.isEmpty()) {
            petCardMapper.insertDogTags(petId, tagIds);
        }
    }
    
}
