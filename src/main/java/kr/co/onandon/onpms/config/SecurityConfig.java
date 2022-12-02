package kr.co.onandon.onpms.config;

import kr.co.onandon.onpms.jwt.JwtEntryPoint;
import kr.co.onandon.onpms.jwt.JwtFilter;
import kr.co.onandon.onpms.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtEntryPoint jwtEntryPoint;
    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();

        http.csrf().disable();

        // Exception 처리
        http.exceptionHandling()
            .authenticationEntryPoint(jwtEntryPoint);

        /*
        *
        * Session 정책 설정
        *
        * ALWAYS: Spring Security Session 사용
        * IF_REQUIRED: Spring Security가 필요시 생성(Default)
        * NEVER: Spring Security가 생성하지 않지만, 기존에 존재하면 사용
        * STATELESS: Spring Security가 생성하지도 기존것을 사용하지도 않음(토큰 방식 사용 시)
        *
        * */
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        // 인증 처리를 Custom한 filter로 할 수 있게 filter 등록
        http.addFilterBefore(new JwtFilter(jwtProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests((authorize) -> authorize
                .antMatchers("/joinForm", "/join", "/login").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
