package ganadinote.common.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 'member' 테이블과 매핑되는 엔티티 클래스입니다. (상태 및 역할 관리 개선)
 */
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mbr_cd")
    private Integer mbrCd;

    @Column(name = "mbr_email", nullable = false, unique = true, length = 100)
    private String mbrEmail;

    @Column(name = "mbr_pw", nullable = false, length = 255)
    private String mbrPw;

    @Column(name = "mbr_nknm", nullable = false, unique = true, length = 50)
    private String mbrNknm;

    // 1. String 타입 대신 MemberStatus enum 타입을 사용합니다.
    @Enumerated(EnumType.STRING) // DB에 enum의 이름(e.g., "ACTIVE")을 문자열로 저장합니다.
    @Column(name = "mbr_status", nullable = false, length = 20)
    private MemberStatus mbrStatus;

    // 2. 회원의 역할을 구분하기 위한 필드를 추가합니다. (e.g., 일반 사용자, 관리자)
    @Enumerated(EnumType.STRING)
    @Column(name = "mbr_role", nullable = false, length = 20)
    private MemberRole mbrRole;

    @CreationTimestamp
    @Column(name = "mbr_reg_date", updatable = false)
    private LocalDateTime mbrRegDate;

    @UpdateTimestamp
    @Column(name = "mbr_mdfcn_date")
    private LocalDateTime mbrMdfcnDate;

    @Builder
    public Member(String mbrEmail, String mbrPw, String mbrNknm) {
        this.mbrEmail = mbrEmail;
        this.mbrPw = mbrPw;
        this.mbrNknm = mbrNknm;
        this.mbrStatus = MemberStatus.ACTIVE; // 회원가입 시 기본 상태를 'ACTIVE'로 설정
        this.mbrRole = MemberRole.USER;     // 회원가입 시 기본 역할을 'USER'로 설정
    }
}
