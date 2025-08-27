package ganadinote.auth.register.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ganadinote.auth.register.dto.RegisterDTO;
import ganadinote.auth.register.mapper.RegisterMapper;
import ganadinote.auth.register.service.MailService;
import ganadinote.auth.register.service.RegisterService;
import ganadinote.common.domain.Member;
import ganadinote.common.domain.type.MemberStatus;
import lombok.RequiredArgsConstructor;

import ganadinote.auth.register.dto.EmailVerificationDTO;
import ganadinote.auth.register.mapper.EmailVerificationMapper;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final RegisterMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationMapper emailVerificationMapper;
    private final MailService mailService;

    @Override
    public boolean isEmailDuplicate(String mbrEmail) {
        return memberMapper.existsByMbrEmail(mbrEmail);
    }

    @Override
    public boolean isNicknameDuplicate(String mbrNknm) {
        return memberMapper.existsByMbrNknm(mbrNknm);
    }

    @Transactional
    @Override
    public void register(RegisterDTO registerDTO) {
        if (!registerDTO.getMbrPw().equals(registerDTO.getConfirmPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (memberMapper.existsByMbrEmail(registerDTO.getMbrEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (memberMapper.existsByMbrNknm(registerDTO.getMbrNknm())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String encodedPassword = passwordEncoder.encode(registerDTO.getMbrPw());

        Member member = new Member(
            registerDTO.getMbrEmail(),
            encodedPassword,
            registerDTO.getMbrNknm(),
            MemberStatus.ACTIVE
        );

        memberMapper.insertMember(member);
    }

    @Override
    public void sendVerificationCode(String mbrEmail) {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        EmailVerificationDTO existingDto = emailVerificationMapper.findByEmail(mbrEmail);

        if (existingDto != null) {
            existingDto.setEmailSendCd(code);
            existingDto.setEmailFtime(LocalDateTime.now().plusMinutes(5));
            emailVerificationMapper.updateVerificationCode(existingDto);
        } else {
            EmailVerificationDTO newDto = new EmailVerificationDTO();
            newDto.setEmail(mbrEmail);
            newDto.setEmailSendCd(code);
            newDto.setEmailRegDate(LocalDateTime.now());
            newDto.setEmailFtime(LocalDateTime.now().plusMinutes(5));
            emailVerificationMapper.insertVerificationCode(newDto);
        }
        
        // 아래 주석을 제거하여 이메일 전송 코드를 활성화합니다.
        mailService.sendEmail(mbrEmail, "회원가입 인증번호", "인증번호: " + code);
    }

    @Override
    public boolean verifyCode(String mbrEmail, String code) {
        EmailVerificationDTO storedDto = emailVerificationMapper.findByEmail(mbrEmail);
        
        if (storedDto != null && storedDto.getEmailSendCd().equals(code) && storedDto.getEmailFtime().isAfter(LocalDateTime.now())) {
            emailVerificationMapper.deleteByEmail(mbrEmail);
            return true;
        }
        return false;
    }
}