package TravelJournal.security.jwt;

import TravelJournal.service.UserDetailsImplementation;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.SignatureException;
import java.util.Date;

//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.UnsupportedJwtException;

/**
 * JSON Web Token Utils class.
 * Methods allows to generate token, get username from token and validate token.
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${traveljournal.app.jwtSecret}")
    private String jwtSecret;

    @Value("${traveljournal.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Generate JSON Web Token.
     * @param authentication -  Authentication Instance from Spring Security
     * @return String token
     */
    public String generateJwtToken(Authentication authentication) {

        UserDetailsImplementation userPrincipal = (UserDetailsImplementation) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Get username from Token.
     * @param token - JSON Web Token.
     * @return username of token.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Validate Token
     * @param authToken - Token to be validated.
     * @return - whether token is valid.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
