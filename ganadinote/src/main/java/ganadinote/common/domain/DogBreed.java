package ganadinote.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DogBreed {
    private int 		breedCd;                   // 품종 코드 (Primary Key, 자동 증가)
    private String 		breedName;              // 품종명 (필수)
    private float 		minTemp;                 // 최저 적정 온도 (필수)
    private float 		maxTemp;                 // 최고 적정 온도 (필수)
    private Boolean 	coldSensitive;         // 추위 민감성
    private Boolean 	heatSensitive;         // 더위 민감성
    private String 		dogSize;                // 강아지 크기
    private String 		activityLevel;          // 활동 수준
    private String 		description;            // 상세 설명
}