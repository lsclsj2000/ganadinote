package ganadinote.petCard.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ganadinote.common.domain.PetCard;
import ganadinote.common.domain.Tag;
import ganadinote.petCard.domain.MemberHeader;

public interface PetCardService {
	
	// 펫 카드 - 회원 헤더
	MemberHeader getHeader(Integer mbrCd);
	// 펫 카드
    List<PetCard> getPetCards(Integer mbrCd);
    
    // 펫 카드 수정 - 전체 태그 불러오기
    List<Tag> getAllTags();

    // 펫 카드 수정 - 로그인 회원 카드
    boolean isOwnerOfPetCard(Integer mbrCd, Integer petId);

    // 펫 카드 수정 - 사진 저장
    String saveAndUpdateCardImage(Integer petId, MultipartFile imageFile);

    // 펫 카드 수정 - 기존 내역 불러오기
    void updateIntroduction(Integer petId, String intro);

    // 펫 카드 수정 - 태그 수정
    void replaceTagsByNames(Integer petId, List<String> tagNames);
    
    // 펫 카드 수정 - 카드별 펫 아이디 불러오기
    Integer getPetIdByCardId(Integer cardId);
    
    // 펫 카드 수정 - 사진 업데이트
    String  saveAndUpdateCardImageByPetId(Integer petId, MultipartFile imageFile);
    
}