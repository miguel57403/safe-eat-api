package ipb.pt.safeeat.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import ipb.pt.safeeat.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class TokenService {

    public String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("id", user.getId())
                .withExpiresAt(Instant.from(LocalDateTime.now().plusHours(24)))
                .sign(Algorithm.HMAC256("abobora" + user.getPassword()));
    }
}
