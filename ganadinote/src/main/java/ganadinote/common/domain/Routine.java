package ganadinote.common.domain;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data // Getter, Setter, toString 등을 자동으로 만들어주는 Lombok 어노테이션
public class Routine {
    private Long routineCd;
    private int petCd;
    private int mbrCd; // member cd도 관리하면 나중에 편리할 수 있습니다. DB 테이블 수정이 필요할 수 있습니다.
    private String routineTitle;
    private String routineRepeatType; // 예: "DAILY", "WEEKLY", "MONTHLY"
    private LocalTime routineTimeOfDay;
    private LocalDate routineStartDate;
    private LocalDate routineEndDate;
    private LocalDateTime regYmdt;
    private LocalDateTime mdfcnYmdt;
}