package me.bhradec.blog.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.access-token.secret}")
    private String accessTokenSecret;

    @Value("${jwt.access-token.ttl}")
    private Long accessTokenTtl;

    @Value("${jwt.access-token.issuer}")
    private String accessTokenIssuer;

    public String extractSubject(String token) {
        log.debug("Extracting subject from JWT {}", token);

        return JWT.require(Algorithm.HMAC512(accessTokenSecret))
                .build()
                .verify(token)
                .getSubject();
    }

    public String generateToken(UserDetails userDetails) {
        log.debug("Generating JWT for user with username {}", userDetails.getUsername());

        Instant now = Instant.now();

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer(accessTokenIssuer)
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(Duration.ofSeconds(accessTokenTtl).toMillis()))
                .sign(Algorithm.HMAC512(accessTokenSecret));
    }

    public Boolean isTokenValid(String token) {
        log.debug("Validating JWT {}", token);

        try {
            JWT.require(Algorithm.HMAC512(accessTokenSecret))
                    .build()
                    .verify(token);

            return true;
        } catch (JWTVerificationException exception) {
            log.debug("Invalid JWT: {}", exception.getMessage());
            return false;
        }
    }
}
