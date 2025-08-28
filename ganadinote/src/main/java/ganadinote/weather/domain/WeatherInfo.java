package ganadinote.weather.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherInfo {
    private Current current;
    private List<Hourly> hourly;
    private List<Daily> daily;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        private Float temp;
        private Float feels_like;
        private Integer humidity;
        private Float wind_speed;
        private Integer uvi;
        private Long sunrise;
        private List<Weather> weather;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hourly {
        private Long dt;
        private Float temp;
        private Float pop; // 강수 확률
        private List<Weather> weather;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Daily {
        private Long dt;
        private DailyTemp temp;
        private List<Weather> weather;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DailyTemp {
        private Float min;
        private Float max;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private Integer id;
        private String description;
        private String icon;
    }

    // 알림 스케줄러를 위한 헬퍼 메소드
    public Float getTemp() {
        return (current != null) ? current.getTemp() : null;
    }
    
    public boolean isRaining() {
        if (current == null || current.getWeather() == null || current.getWeather().isEmpty()) {
            return false;
        }
        Integer weatherId = current.getWeather().get(0).getId();
        return (weatherId >= 200 && weatherId < 600);
    }
}