package ganadinote.auth.register.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ganadinote.auth.register.dto.EmailVerificationDTO;

@Mapper
public interface EmailVerificationMapper {
    /**
     * 이메일 인증 코드를 데이터베이스에 삽입합니다.
     * 동일한 이메일이 이미 존재하면 업데이트합니다.
     * @param dto 이메일 인증 정보 DTO
     */
    void insertVerificationCode(EmailVerificationDTO dto);

    /**
     * 기존에 존재하는 인증 코드를 업데이트합니다.
     * @param dto 업데이트할 이메일 인증 정보 DTO
     */
    void updateVerificationCode(EmailVerificationDTO dto);

    /**
     * 이메일로 인증 정보를 조회합니다.
     * @param email 조회할 이메일
     * @return 이메일 인증 정보 DTO
     */
    EmailVerificationDTO findByEmail(@Param("email") String email);

    /**
     * 인증 성공 시 이메일 인증 코드를 삭제합니다.
     * @param email 삭제할 이메일
     */
    void deleteByEmail(@Param("email") String email);
}