package kr.co.onandon.onpms.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Header에 있는 token 정보 추출
        String token = getJwtFromRequest((HttpServletRequest) request);

        // redis 객체 호출
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        if (token != null) {
            String mberSn = operations.get(token);

            if (mberSn.length() > 0) {
                boolean isValidToken = jwtProvider.validateToken(token);


                if (isValidToken) {
                    UserDetails userDetails = jwtProvider.getUserDetails(mberSn);

                    UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                } else {
                    request.setAttribute("unauthorization", "002 Authentication key expiration.");
                }
            } else {
                request.setAttribute("unauthorization", "001 Not an authorized user.");
            }
        } else {
            request.setAttribute("unauthorization", "No authentication key.");
        }
        // 사용자 인증 안된 상태에서  dofilter 호출 시 JwtEntryPoint로 전달
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String returnValue = null;

        String authPrefix = "Bearer ";

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasLength(bearerToken) && bearerToken.startsWith(authPrefix)) {
            returnValue = bearerToken.substring(authPrefix.length());
        }

        return returnValue;
    }
}
