package ganadinote.common.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class NotificationHistory {

	private Long id; // 고유 식별자
    private Integer mbrCd; // 알림을 받은 회원 코드
    private String title; // 알림 제목 (예: "산책 알림")
    private String message; // 알림 내용 (예: "오늘은 비가 와서...")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt; // 타입도 LocalDateTime으로 변경
    private boolean isRead; // 사용자가 알림을 읽었는지 여부
}
