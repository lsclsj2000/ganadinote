package ganadinote.auth.pet.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class PetDTO {
    private String petId;
    private String userId;
    private String petName;
    private int petBreedId;
    private String petGender;
    private Date petBirthDate;
    private double petWeight;
    private String petColor;
    private String petProfileImgUrl;
}
