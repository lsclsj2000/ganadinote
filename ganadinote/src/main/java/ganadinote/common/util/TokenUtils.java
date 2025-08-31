package ganadinote.common.util;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TokenUtils {

    /**
     * 현재 HTTP 요청에서 회원 코드(mbrCd)를 추출하여 반환합니다.
     * @return mbrCd (로그인 정보가 없으면 null)
     */
    public static String getMbrCd() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                // JwtTokenUtil 객체의 정적 메소드로 접근
                return JwtTokenUtil.getInstance().getSubject(jwt);
            }
        } catch (Exception e) {
            // 요청 컨텍스트가 없거나 토큰이 유효하지 않은 경우
            return null;
        }
        return null;
    }
}