package ganadinote.auth.pet.service;

import ganadinote.auth.pet.dto.PetDTO;
import org.springframework.web.multipart.MultipartFile;
import ganadinote.common.file.FileUtils;

public interface PetService {
    boolean registerPet(PetDTO petDTO, MultipartFile petProfileImg, FileUtils fileUtils);
}