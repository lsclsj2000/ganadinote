package ganadinote.notification.domain;

import lombok.Data;

@Data
public class LocationUpdateDTO {
    private Integer mbrCd;
    private Double latitude;
    private Double longitude;
}