package ganadinote.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (개발 편의상)
            .authorizeHttpRequests(authorize -> authorize
                // .requestMatchers(
                //     "/",
                //     "/auth/**",
                //     "/assets/**",
                //     "/api/**",
                //     "/about",
                //     "/contact"
                // ).permitAll()
                .anyRequest().permitAll() // 모든 요청에 대해 인증 없이 접근 허용
            );
        
        return http.build();
    }
}