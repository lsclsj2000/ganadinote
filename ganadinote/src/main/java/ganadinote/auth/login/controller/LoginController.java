package ganadinote.auth.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ganadinote.auth.login.dto.LoginRequestDTO;
import ganadinote.auth.login.dto.LoginResponseDTO;
import ganadinote.auth.login.service.LoginService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        
        LoginResponseDTO response = loginService.login(loginRequestDTO.getMbrEmail(), loginRequestDTO.getMbrPw());
        
        if (response != null && response.getToken() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}