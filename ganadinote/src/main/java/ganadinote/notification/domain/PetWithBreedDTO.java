package ganadinote.notification.domain;

import ganadinote.common.domain.Pet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PetWithBreedDTO extends Pet {
    private String 	breedName;
    private Float 	minTemp;
    private Float 	maxTemp;
    private Boolean coldSensitive;
    private Boolean heatSensitive;
    private String 	dogSize;
    private String 	activityLevel;
}