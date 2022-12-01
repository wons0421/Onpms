package kr.co.onandon.onpms.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kr.co.onandon.onpms.security.CustomUserDetailsService;
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
    public enum TokenKey {
        JWT_BASIC("ONANDON-INFOMATION-JWT-SECRET-TOKEN"),
        JWT_REFRESH("ONANDON-INFOMATION-JWT-REFRESH-TOKEN");

        private final String value;

        TokenKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ValidateResult {
        OK, DIFFERENT_KEY, ERROR
    }

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

    public String getToken(int mberSn, TokenKey key) {
        return generateToken(mberSn, getKey(key.value));
    }

    public UserDetails getUserDetails(String mberSn) {
        return detailsService.loadUserByUsername(mberSn);
    }

    public ValidateResult validateToken(String token, String key) {
        ValidateResult returnValue = ValidateResult.ERROR;

        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey(key))
                .build()
                .parseClaimsJws(token);

            returnValue = ValidateResult.OK;
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
            returnValue = ValidateResult.DIFFERENT_KEY;
        }

        return returnValue;
    }
}
