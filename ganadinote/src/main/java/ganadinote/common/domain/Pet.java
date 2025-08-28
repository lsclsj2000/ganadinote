package ganadinote.common.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
	private int 			petCd;                   // 반려동물 코드 (Primary Key, 자동 증가)
    private int 			mbrCd;                   // 회원 코드 (Foreign Key)
    private String 			petName;              // 반려동물 이름 (필수)
    private String 			petBreed;             // 반려동물 품종
    private String 			petGender;            // 반려동물 성별
    private LocalDate 		petBirthDate;      // 반려동물 생년월일
    private BigDecimal 		petWeight;        // 반려동물 체중
    private String 			petProfileImgUrl;     // 반려동물 프로필 이미지 URL
    private String 			petIntroduction;      // 반려동물 소개
    private LocalDateTime 	petRegDate;    // 등록 날짜 (자동 생성)
    private LocalDateTime 	petMdfcnDate;  // 수정 날짜 (자동 업데이트)
}
