package catchytape.spring.auth.service;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import catchytape.spring.auth.controller.dto.UserAuthResponse;
import catchytape.spring.common.exception.CatchyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private final int HOUR_TO_SECONDS = 60*60;
    private final int WEEK_TO_SECONDS = 60*60*24*7;

    @PostConstruct /* 의존성 주입이 이루어진 뒤 초기화를 수행 */
    protected void init() {
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key= Keys.hmacShaKeyFor(encodedKey.getBytes());
    }


    public UserAuthResponse generateJwtToken(String userId, String refreshId) {
        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + HOUR_TO_SECONDS);
        String accessToken = Jwts.builder()
                .claim("user_id", userId)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, signatureAlgorithm)
                .compact();

        Date refreshTokenExpiresIn = new Date(now + WEEK_TO_SECONDS);
        String refreshToken = Jwts.builder()
                .claim("refresh_id", refreshId)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, signatureAlgorithm)
                .compact();

        return UserAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    
    public String decodeToken(String key, String refreshToken) {
        Claims claims = Jwts.parserBuilder()
            .build()
            .parseClaimsJwt(refreshToken)
            .getBody();

        return (String) claims.get(key);
    }

    public boolean isValidToken(String token) throws CatchyException {
        try {
            Jwts.parserBuilder().build().parseClaimsJws(token);

            return true;
        } catch(SecurityException | MalformedJwtException e) {
            throw new CatchyException("NOT_EXIST_REFRESH_TOKEN", "WRONG_TOKEN");
        } catch (ExpiredJwtException e) {
            throw new CatchyException("EXPIRED_TOKEN", "WRONG_TOKEN");
        }
    }
}
