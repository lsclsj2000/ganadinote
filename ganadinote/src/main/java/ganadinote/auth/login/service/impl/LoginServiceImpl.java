package ganadinote.auth.login.service.impl;

import ganadinote.auth.login.dto.LoginResponseDTO;
import ganadinote.auth.login.mapper.LoginMapper;
import ganadinote.auth.login.service.LoginService;
import ganadinote.common.domain.Member;
import ganadinote.common.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // log 추가
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {
    private final LoginMapper loginMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public LoginResponseDTO login(String mbrEmail, String mbrPw) {
        // 이메일로 회원 정보 조회
        Member member = loginMapper.findByMbrEmail(mbrEmail);

        if (member == null) {
            log.warn("로그인 실패: 존재하지 않는 이메일입니다. -> {}", mbrEmail);
            return null;
        }

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(mbrPw, member.getMbrPw())) {
            log.warn("로그인 실패: 비밀번호가 일치하지 않습니다. -> {}", mbrEmail);
            return null; 
        }

        // JWT 토큰 생성
        String token = jwtTokenUtil.generateToken(String.valueOf(member.getMbrCd()));
        log.info("로그인 성공! JWT 토큰이 생성되었습니다. -> {}", member.getMbrEmail());
        
        return new LoginResponseDTO(token);
    }
}