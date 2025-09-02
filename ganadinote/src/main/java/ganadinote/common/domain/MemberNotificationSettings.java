package ganadinote.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberNotificationSettings {

	private int 		mbrCd;
    private JsonNode 	notificationSchedule;
    private Timestamp 	updatedAt;

    // 내부 JSON 구조를 위한 클래스
    public static class NotificationSetting {
        private String 				type;
        private List<ScheduleItem> 	schedules;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ScheduleItem {
            @JsonProperty("day_of_week")
            private int dayOfWeek;
            @JsonProperty("start_time")
            private String startTime;
            @JsonProperty("end_time")
            private String endTime;
        }
    }
}