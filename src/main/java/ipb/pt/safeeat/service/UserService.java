package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.RestrictionConstants;
import ipb.pt.safeeat.constants.UserConstants;
import ipb.pt.safeeat.dto.UserDto;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.CartRepository;
import ipb.pt.safeeat.repository.RestrictionRepository;
import ipb.pt.safeeat.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private RestrictionRepository restrictionRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstants.NOT_FOUND));
    }

    public User create(UserDto userDto) {
        if (userDto.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
        }

        if (!userDto.getRestrictionIds().isEmpty()) {
            for (String restrictionId : userDto.getRestrictionIds()) {
                restrictionRepository.findById(restrictionId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestrictionConstants.NOT_FOUND));
            }
        }

        User user = new User();
        BeanUtils.copyProperties(userDto, user);

        Cart cart = cartRepository.save(new Cart());
        user.setCart(cart);

        return userRepository.save(user);
    }

    @Transactional
    public List<User> createMany(List<UserDto> userDtos) {
        List<User> created = new ArrayList<>();
        for (UserDto userDto : userDtos) {
            created.add(create(userDto));
        }

        return created;
    }

    public User update(UserDto userDto) {
        User old = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstants.NOT_FOUND));

        BeanUtils.copyProperties(userDto, old);
        return userRepository.save(old);
    }

    public void delete(String id) {
        userRepository.deleteById(id);
    }
}
