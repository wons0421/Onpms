package kr.co.onandon.onpms.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;


@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // redis 객체 호출
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // Header에 있는 token 정보 추출
        String token = getJwtFromRequest((HttpServletRequest) request);

        JwtProvider.ValidateResult isValidToken;

        // token 없으면 아웃
        if (token == null) {
            request.setAttribute("unauthorization", "No authentication key.");
            filterChain.doFilter(request, response);
            return;
        }

        String mberSn = operations.get(token);

        // token이 redis에 없으면 아웃
        if (mberSn == null) {
            request.setAttribute("unauthorization", "001 Not an authorized user.");
            filterChain.doFilter(request, response);
            return;
        }

        isValidToken =
            jwtProvider.validateToken(
                token,
                JwtProvider.TokenKey.JWT_BASIC.getValue()
            );

        // return 값이 DIFFERENT_KEY이면 키값이 refresh token 일수도 있으니 다시 한번 체크
        if (JwtProvider.ValidateResult.DIFFERENT_KEY.equals(isValidToken)) {
            isValidToken =
                jwtProvider.validateToken(
                    token,
                    JwtProvider.TokenKey.JWT_REFRESH.getValue()
                );

            token = jwtProvider.getToken(
                Integer.parseInt(mberSn),
                JwtProvider.TokenKey.JWT_BASIC
            );

            operations.set(token, ObjectUtils.getDisplayString(mberSn));

            redisTemplate.expire(token, Duration.ofSeconds(60));

            response.addHeader("newtoken", token);
        }

        // validation 통과 못하면 아웃
        if (!JwtProvider.ValidateResult.OK.equals(isValidToken)) {
            request.setAttribute("unauthorization", "002 Authentication key expiration.");
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = jwtProvider.getUserDetails(mberSn);

        UsernamePasswordAuthenticationToken authenticationToken
            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

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
