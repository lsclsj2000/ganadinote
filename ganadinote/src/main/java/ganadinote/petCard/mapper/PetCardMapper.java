package ganadinote.petCard.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import ganadinote.common.domain.PetCard;
import ganadinote.common.domain.Tag;
import ganadinote.petCard.domain.MemberHeader;

@Mapper
public interface PetCardMapper {
	
	// 펫 카드 - 회원 헤더
	MemberHeader selectHeaderByMbrCd(@Param("mbrCd") Integer mbrCd);

	// 펫 카드
    List<PetCard> selectPetCardsByMbrCd(@Param("mbrCd") Integer mbrCd);

    // 펫 카드 - 태그
    List<String> selectTagNamesByCardId(@Param("cardId") Integer cardId);
    
    // NEW: 수정 화면용
    List<Tag> selectAllTags();

    // NEW: 보안 체크
    Integer selectOwnerMbrByCardId(@Param("cardId") Integer cardId);

    // NEW: 업데이트들
    int updatePetImageByCardId(@Param("cardId") Integer cardId, @Param("url") String imageUrl);
    int updateCardIntroduction(@Param("cardId") Integer cardId, @Param("intro") String introduction);
    String selectPetImageUrlByCardId(@Param("cardId") Integer cardId);

    // NEW: 태그 교체
    List<Integer> selectTagIdsByNames(@Param("names") List<String> names);
    int deleteDogTagsByCardId(@Param("cardId") Integer cardId);
    int insertDogTags(@Param("cardId") Integer cardId, @Param("tagIds") List<Integer> tagIds);
    
}
