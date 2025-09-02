package ganadinote.auth.pet.service;

import ganadinote.auth.pet.dto.PetDTO;
import org.springframework.web.multipart.MultipartFile;

public interface PetService {
    boolean registerPet(PetDTO petDTO, MultipartFile petProfileImg);
}