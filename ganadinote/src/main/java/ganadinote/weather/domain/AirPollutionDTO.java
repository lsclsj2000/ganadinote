package ganadinote.weather.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirPollutionDTO {
    private List<AirPollutionList> list;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AirPollutionList {
        private Components components;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Components {
        @JsonProperty("pm2_5")
        private Float pm25;
        private Float pm10;
    }

    // 알림 스케줄러를 위한 헬퍼 메소드
    public Float getPm25() {
        if (list == null || list.isEmpty() || list.get(0).getComponents() == null) {
            return 0.0f;
        }
        return list.get(0).getComponents().getPm25();
    }

    public Float getPm10() {
        if (list == null || list.isEmpty() || list.get(0).getComponents() == null) {
            return 0.0f;
        }
        return list.get(0).getComponents().getPm10();
    }
}