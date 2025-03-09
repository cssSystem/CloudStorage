package sys.tem.cloudservice.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class Generator {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private Long expiration = 3600000L;
    private String messageErr = "Не коректен ключ";

    public String generateToken(Authentication authentication) {
        final var username = authentication.getName();
        final var issuedAtDate = new Date();
        final var expirationDate = new Date(issuedAtDate.getTime() + expiration);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAtDate)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException(messageErr, e.fillInStackTrace());
        }
    }
}
