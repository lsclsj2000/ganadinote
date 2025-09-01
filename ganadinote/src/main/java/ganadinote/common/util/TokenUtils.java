package ganadinote.common.util;

import jakarta.servlet.http.Cookie; // [염가은 2025-09-01] Cookie 클래스를 import 합니다.
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
            String jwt = null;

            // [염가은 2025-09-01] START: 헤더와 쿠키 양쪽에서 토큰을 찾도록 로직을 수정합니다.
            
            // 1. 먼저 Authorization 헤더에서 토큰을 찾습니다. (AJAX, fetch 요청을 위함)
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }

            // 2. 만약 헤더에 토큰이 없다면, 쿠키에서 토큰을 찾습니다. (일반 페이지 이동을 위함)
            if (jwt == null && request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    // LoginController에서 설정한 쿠키 이름("Authorization")과 일치하는 것을 찾습니다.
                    if ("Authorization".equals(cookie.getName())) { 
                        String cookieValue = cookie.getValue();
                        // 쿠키 값에 'Bearer '가 포함되어 있을 수 있으므로, 제거 후 순수 토큰 값만 사용합니다.
                        if (cookieValue != null) {
                            jwt = cookieValue.replace("Bearer ", "").trim();
                        }
                        break; // 토큰을 찾았으면 반복을 중단합니다.
                    }
                }
            }

            // [염가은 2025-09-01] END

            // 최종적으로 토큰을 찾았다면, JwtTokenUtil을 통해 회원 코드를 추출합니다.
            if (jwt != null && !jwt.isEmpty()) {
                return JwtTokenUtil.getInstance().getSubject(jwt);
            }

        } catch (Exception e) {
            // 요청 컨텍스트가 없거나 토큰이 유효하지 않은 경우
            // (선택) 로그를 남기면 디버깅에 도움이 됩니다. e.g., log.error(...)
            return null;
        }
        return null;
    }
}