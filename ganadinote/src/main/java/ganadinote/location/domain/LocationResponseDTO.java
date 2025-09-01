package ganadinote.location.domain;

import lombok.Data;

@Data
public class LocationResponseDTO {
	private String locationName;
	private String error;
	private Double latitude;
    private Double longitude;
}
