package ipb.pt.safeeat.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import ipb.pt.safeeat.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    private final static String SECRET = "secret";

    public String generateToken(User user) {
        return JWT.create()
                .withIssuer("Products")
                .withSubject(user.getEmail())
                .withClaim("id", user.getId())
                .withExpiresAt(LocalDateTime.now()
                        .plusMinutes(30)
                        .toInstant(ZoneOffset.of("-03:00"))
                )
                .sign(Algorithm.HMAC256(SECRET));
    }

    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .withIssuer("Products")
                .build().verify(token).getSubject();
    }
}
