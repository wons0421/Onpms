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
    private final String JWT_SECRET = "ONANDON-INFOMATION-JWT-SECRET-TOKEN";
    private final String JWT_REFRESH = "ONANDON-INFOMATION-JWT-REFRESH-TOKEN";

    private final int JWT_EXPIRATION_TIME = 60 * 60 * 60 * 60;

    private final CustomUserDetailsService detailsService;

    private String generateToken(int mberSn, Date expiryDate, Key key) {
        return Jwts.builder()
            .setSubject(ObjectUtils.getDisplayString(mberSn))
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    private Key getKey() {

        byte[] keyBytes
            = JWT_SECRET.getBytes();

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getToken(int mberSn) {
        Date date
            = new Date();

        Date expiryDate
            = new Date(date.getTime() + JWT_EXPIRATION_TIME);

        return generateToken(mberSn, expiryDate, getKey());
    }

    private Claims parseJwtToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public UserDetails getIdFromJwtToken(String token) {
        return detailsService.loadUserByUsername(parseJwtToken(token).getSubject());
    }

    public boolean validateToken(String token) {
        boolean returnValue = false;

        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);

            returnValue = true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            ex.printStackTrace();
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return returnValue;
    }
}
