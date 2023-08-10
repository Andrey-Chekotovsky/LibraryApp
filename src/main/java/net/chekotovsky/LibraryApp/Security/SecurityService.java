package net.chekotovsky.LibraryApp.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import net.chekotovsky.LibraryApp.Dao.UserDao;
import net.chekotovsky.LibraryApp.Exceptions.AuthException;
import net.chekotovsky.LibraryApp.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SecurityService {
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Integer expirationInSeconds;
    @Value("${jwt.issuer}")
    private String issuer;

    private TokenDetails generateToken(User user){
        Map<String, Object> claims = new HashMap<>(){{
           put("role", user.getRole());
           put("username", user.getUsername());
        }};
        return generateToken(claims, user.getId().toString());
    }
    private TokenDetails generateToken(Map<String, Object> claims, String subject){
        Long expirationInMillis = expirationInSeconds * 1000L;
        Date expirationDate = new Date(new Date().getTime() + expirationInMillis);
        return generateToken(expirationDate, claims, subject);
    }
    private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject){
        Date creationDate = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(creationDate)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();
        return TokenDetails.builder()
                .token(token)
                .issueDate(creationDate)
                .expiresDate(expirationDate)
                .build();
    }
    public Mono<TokenDetails> authenticate(String username, String password)
    {
        return userDao.selectByUsername(username)
                .flatMap(user -> {
                    if (!user.isEnabled()){
                        return Mono.error(new AuthException("Account is not enabled", "LIBRARY_USER_IS_DISABLED"));
                    }
                    if (!passwordEncoder.matches(password, user.getPassword())){
                        return Mono.error(new AuthException("Invalid password", "LIBRARY_INVALID_PASSWORD"));
                    }
                    return Mono.just(generateToken(user).toBuilder()
                            .userId(user.getId())
                            .build());
                }).switchIfEmpty(Mono.error(new AuthException("Invalid username", "LIBRARY_INVALID_USERNAME")));
    }
}
