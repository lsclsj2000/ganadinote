package ganadinote.auth.pet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ganadinote.auth.pet.dto.PetDTO;
import ganadinote.auth.pet.service.PetService;
import ganadinote.common.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import ganadinote.common.file.FileUtils; // FileUtils import

@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final ObjectMapper objectMapper;
    private final FileUtils fileUtils; // FileUtils 주입

    @GetMapping("/register")
    public String showPetRegistrationPage() {
        return "auth/auth-petInfo";
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerPet(
            @RequestPart("petData") String petDataJson,
            @RequestPart(value = "petProfileImg", required = false) MultipartFile petProfileImg) {
        
        try {
            PetDTO petDTO = objectMapper.readValue(petDataJson, PetDTO.class);
            
            String userId = TokenUtils.getMbrCd();

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 없습니다.");
            }
            
            petDTO.setUserId(userId);

            // 서비스 레이어 호출 시 MultipartFile과 FileUtils 함께 전달
            boolean isRegistered = petService.registerPet(petDTO, petProfileImg, fileUtils);

            if (isRegistered) {
                return ResponseEntity.ok("반려견 등록이 완료되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("반려견 등록에 실패했습니다.");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 데이터 형식입니다.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 중 오류가 발생했습니다.");
        }
    }
}