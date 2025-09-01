package ganadinote.auth.login.controller;

import ganadinote.auth.login.dto.LoginRequestDTO;
import ganadinote.auth.login.dto.LoginResponseDTO;
import ganadinote.auth.login.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        
        LoginResponseDTO loginResponse = loginService.login(loginRequestDTO.getMbrEmail(), loginRequestDTO.getMbrPw());
        
        if (loginResponse != null && loginResponse.getToken() != null) {
            
            // [염가은 2025-09-01] 쿠키 값에 공백이 포함되지 않도록 "Bearer " 접두사를 제거합니다.
            String token = loginResponse.getToken();
            Cookie cookie = new Cookie("Authorization", token); 
            
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            
            response.addCookie(cookie);
            
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}