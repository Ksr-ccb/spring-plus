package org.example.expert.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 스프링시큐리티 기능 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        System.out.println("in SecurityConfig");

        return http
                //이건 basic auth 사용안한다는 표기인ㄷㅔ REST API 사용중이라 disable 해놓으면됨
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // [2] CSRF 비활성화 ( REST API + JWT 환경에서는 CSRF 필요 없음 → `csrf().disable()` 붙이면 된다고함. )
                .csrf(AbstractHttpConfigurer::disable)

                // [3] 세션 STATELESS 설정 ( JWT는 세션 안 씀 →
                //  `sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)` 붙이면된다.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // [4] URL별 권한 설정
                .authorizeHttpRequests(auth-> auth
                        // [5] 인증/인가 정책 설정
                        .requestMatchers("/auth/**", "/actuator/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // [6] JWT 필터 삽입
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                // [7] 예외 처리 커스터마이징
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
                        })
                )

                // [8] CORS 설정 (필요 시)

                .build(); // [9] SecurityFilterChain 반환
    }
}
