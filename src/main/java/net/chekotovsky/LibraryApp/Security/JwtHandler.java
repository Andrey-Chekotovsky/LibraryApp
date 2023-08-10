package net.chekotovsky.LibraryApp.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import net.chekotovsky.LibraryApp.Exceptions.AuthException;
import net.chekotovsky.LibraryApp.Exceptions.UnauthorisedException;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Date;

public class JwtHandler {
    private String secret;

    public JwtHandler(String secret) {
        this.secret = secret;
    }

    public Mono<VerificationResult> check(String accessToken){
        return Mono.just(verify(accessToken))
                .onErrorResume(e -> Mono.error(new UnauthorisedException(e.getMessage())));
    }
    private VerificationResult verify(String token){
        Claims claims = getClaimsFromToken(token);
        Date experationDate = claims.getExpiration();
        if (experationDate.before(new Date()))
            throw new AuthException("Token expired", "LIBRARY_TOKEN_EXPIRED");
        return new VerificationResult(claims, token);
    }
    private Claims getClaimsFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public static class VerificationResult{
        public Claims claims;
        public String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }
    }
}
