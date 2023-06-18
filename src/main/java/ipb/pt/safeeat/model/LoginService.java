package ipb.pt.safeeat.model;

import ipb.pt.safeeat.constants.UserConstants;
import ipb.pt.safeeat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LoginService {
    @Autowired
    private UserRepository userRepository;

    public LoginResponse login(LoginBody loginBody){
        User user = userRepository.findByEmail(loginBody.getEmail()).stream().findFirst().orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstants.NOT_FOUND));

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setStatus("success");
        loginResponse.setMessage("Login successful");
        loginResponse.setToken(user.getId());

        return loginResponse;
    }
}
