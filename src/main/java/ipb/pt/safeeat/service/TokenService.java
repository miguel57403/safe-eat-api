package ipb.pt.safeeat.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import ipb.pt.safeeat.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    public String generateToken(User user) {
        return JWT.create()
                .withIssuer("/**")
                .withSubject(user.getEmail())
                .withClaim("id", user.getId())
                .withExpiresAt(LocalDateTime.now()
                        .plusMinutes(30)
                        .toInstant(ZoneOffset.of("-03:00"))
                )
                .sign(Algorithm.HMAC256("secret"));
    }
}
