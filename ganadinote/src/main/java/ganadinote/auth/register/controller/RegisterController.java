package ganadinote.auth.register.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ganadinote.auth.register.dto.RegisterDTO;

/**
 * 회원가입 관련 웹 요청을 처리하는 컨트롤러입니다.
 */
@Controller
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다.
public class RegisterController {

    //private final RegisterService registerService;

    /**
     * 회원가입 페이지를 보여주는 메소드입니다.
     * GET 요청이 "/auth/register" 경로로 들어올 때 호출됩니다.
     * @return 보여줄 HTML 파일의 이름 (templates/auth-register.html)
     */
    @GetMapping("/auth-register.html")
    public String showRegisterForm() {
        return "ex/auth-register";
    }

    /**
     * 회원가입 폼에서 전송된 데이터를 처리하는 메소드입니다.
     * POST 요청이 "/auth/register" 경로로 들어올 때 호출됩니다.
     * @param requestDto HTML 폼에서 전송된 데이터 (email, password, nickname)
     * @return 회원가입 성공 후 이동할 페이지 경로 (로그인 페이지로 리다이렉트)
     */
    @PostMapping("/register")
    public String processRegister(RegisterDTO regosterDTO) {
        // 서비스 클래스에 회원가입 처리를 위임합니다.
        //registerService.register(regosterDTO);

        // 회원가입이 성공하면 로그인 페이지로 이동시킵니다.
        return "redirect:/auth/login";
    }
}
