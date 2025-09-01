package ganadinote.location.domain;

import lombok.Data;

@Data
public class LocationDTO {
	private double latitude;
	private double longitude;
	private String locationName;
	
	
	/*
	 * public double getLatitude() { return latitude; }
	 * 
	 * public void setLatitude(double latitude) { this.latitude = latitude; }
	 * 
	 * public double getLongitude() { return longitude; }
	 * 
	 * public void setLongitude(double longitude) { this.longitude = longitude; }
	 */
}
