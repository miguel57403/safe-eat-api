package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.RestrictionConstants;
import ipb.pt.safeeat.constants.UserConstants;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.*;
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

    public User create(User user) {
        if (user.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
        }

        if (user.getRestrictions() != null && !user.getRestrictions().isEmpty()) {
            List<Restriction> restrictions = new ArrayList<>();
            for (Restriction restriction : user.getRestrictions()) {
                restrictions.add(restrictionRepository.findById(restriction.getId()).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestrictionConstants.NOT_FOUND)));
            }

            user.setRestrictions(restrictions);
        }

        Cart cart = cartRepository.save(new Cart());
        user.setCart(cart);

        return userRepository.save(user);
    }

    @Transactional
    public List<User> createMany(List<User> users) {
        List<User> created = new ArrayList<>();
        for(User user : users) {
            created.add(create(user));
        }

        return created;
    }

    public User update(User user) {
        User old = userRepository.findById(user.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstants.NOT_FOUND));

        BeanUtils.copyProperties(user, old);
        return userRepository.save(user);
    }

    public void delete(String id) {
        userRepository.deleteById(id);
    }
}