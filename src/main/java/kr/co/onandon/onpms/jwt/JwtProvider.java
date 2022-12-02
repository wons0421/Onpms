package kr.co.onandon.onpms.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kr.co.onandon.onpms.security.CustomUserDetailsService;
import kr.co.onandon.onpms.security.SecurityEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.security.Key;
import java.util.Date;
@Log4j2
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final CustomUserDetailsService detailsService;

    private String generateToken(int mberSn, Key key) {
        return Jwts.builder()
            .setSubject(ObjectUtils.getDisplayString(mberSn))
            .setIssuedAt(new Date())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    private Key getKey(String key) {

        byte[] keyBytes
            = key.getBytes();

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getToken(int mberSn, SecurityEnum.TokenKey key) {
        return generateToken(mberSn, getKey(key.getValue()));
    }

    public UserDetails getUserDetails(String mberSn) {
        return detailsService.loadUserByUsername(mberSn);
    }

    public SecurityEnum.ValidateResult validateToken(String token, SecurityEnum.TokenKey key) {
        SecurityEnum.ValidateResult returnValue = SecurityEnum.ValidateResult.ERROR;

        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey(key.getValue()))
                .build()
                .parseClaimsJws(token);

            returnValue = SecurityEnum.ValidateResult.OK;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        } catch (SignatureException e) {
            returnValue = SecurityEnum.ValidateResult.DIFFERENT_KEY;
        }

        return returnValue;
    }
}
