package ganadinote.auth.register.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ganadinote.auth.register.dto.RegisterDTO;
import ganadinote.auth.register.service.RegisterService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "ex/auth-register";
    }

    @PostMapping("/register")
    public String processRegister(RegisterDTO registerDTO, RedirectAttributes redirectAttributes) {
        try {
            registerService.register(registerDTO);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }

        return "redirect:/todo/list";
    }
    
    @GetMapping("/api/register/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam("mbrEmail") String mbrEmail) {
        boolean isDuplicate = registerService.isEmailDuplicate(mbrEmail);
        return ResponseEntity.ok(isDuplicate);
    }
    
    @GetMapping("/api/register/check-nickname")
    @ResponseBody
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam("mbrNknm") String mbrNknm) {
        boolean isDuplicate = registerService.isNicknameDuplicate(mbrNknm);
        return ResponseEntity.ok(isDuplicate);
    }
}