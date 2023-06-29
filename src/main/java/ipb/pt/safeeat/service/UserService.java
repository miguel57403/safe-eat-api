package ipb.pt.safeeat.service;

import ipb.pt.safeeat.dto.UserDto;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.model.Restriction;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.*;
import ipb.pt.safeeat.utility.NotFoundConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AddressRepository addressRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));
    }

    public User create(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");

        List<Restriction> restrictions = getRestrictions(userDto);

        User user = new User();
        BeanUtils.copyProperties(userDto, user);

        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRestrictions(restrictions);

        Cart cart = cartRepository.save(new Cart());
        user.setCart(cart);

        return userRepository.save(user);
    }

    private List<Restriction> getRestrictions(UserDto userDto) {
        List<Restriction> restrictions = new ArrayList<>();
        if (!userDto.getRestrictionIds().isEmpty()) {
            for (String restrictionId : userDto.getRestrictionIds()) {
                restrictions.add(restrictionRepository.findById(restrictionId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTRICTION_NOT_FOUND)));
            }
        }
        return restrictions;
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getId().equals(userDto.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot update other user");

        User byEmail = userRepository.findByEmail(userDto.getEmail()).orElse(null);

        if (byEmail != null && !byEmail.getId().equals(old.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");

        List<Restriction> restrictions = getRestrictions(userDto);

        BeanUtils.copyProperties(userDto, old);
        old.setPassword(passwordEncoder.encode(userDto.getPassword()));
        old.setRestrictions(restrictions);

        return userRepository.save(old);
    }

    public void delete(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        if (!user.getRestaurants().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete user with restaurants");

        paymentRepository.deleteAll(user.getPayments());
        addressRepository.deleteAll(user.getAddresses());
        cartRepository.delete(user.getCart());
        userRepository.deleteById(id);
    }
}
