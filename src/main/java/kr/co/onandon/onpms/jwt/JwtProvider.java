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
        JWT_SECRET("ONANDON-INFOMATION-JWT-SECRET-TOKEN"),
        JWT_REFRESH("ONANDON-INFOMATION-JWT-REFRESH-TOKEN");

        private final String value;

        TokenKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
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
        /*Date date
            = new Date();

        Date expiryDate
            = new Date(date.getTime() + JWT_EXPIRATION_TIME);*/

        return generateToken(mberSn, getKey(key.value));
    }

    /*private Claims parseJwtToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }*/

    public UserDetails getUserDetails(String mberSn) {
        return detailsService.loadUserByUsername(mberSn);
    }

    public boolean validateToken(String token) {
        boolean returnValue = false;

        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey(TokenKey.JWT_SECRET.value))
                .build()
                .parseClaimsJws(token);

            returnValue = true;
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
        }
        return returnValue;
    }
}
