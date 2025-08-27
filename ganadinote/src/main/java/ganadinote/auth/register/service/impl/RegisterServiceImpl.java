package ganadinote.auth.register.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ganadinote.auth.register.dto.RegisterDTO;
import ganadinote.auth.register.mapper.RegisterMapper;
import ganadinote.auth.register.service.RegisterService;
import ganadinote.common.domain.Member;
import ganadinote.common.domain.type.MemberStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final RegisterMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    
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
        // 1. 비밀번호 일치 여부 확인
        if (!registerDTO.getMbrPw().equals(registerDTO.getConfirmPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 2. 이메일 중복 확인
        if (memberMapper.existsByMbrEmail(registerDTO.getMbrEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 3. 닉네임 중복 확인
        if (memberMapper.existsByMbrNknm(registerDTO.getMbrNknm())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 4. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(registerDTO.getMbrPw());

        // 5. Member 객체 생성 및 저장
        Member member = new Member(
            registerDTO.getMbrEmail(),
            encodedPassword,
            registerDTO.getMbrNknm(),
            MemberStatus.ACTIVE
        );

        memberMapper.insertMember(member);
    }
}