package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.UserDto;
import ipb.pt.safeeat.dto.UserUpdateDto;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    private AzureBlobService azureBlobService;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !getAuthenticatedUser().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_USER);

        return user;
    }

    public User create(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");

        if (!userDto.getRestrictionIds().isEmpty()) {
            for (String restrictionId : userDto.getRestrictionIds()) {
                restrictionRepository.findById(restrictionId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTRICTION_NOT_FOUND));
            }
        }

        User user = new User();
        BeanUtils.copyProperties(userDto, user);

        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Cart cart = cartRepository.save(new Cart());
        user.setCartId(cart.getId());

        return userRepository.save(user);
    }

    public User update(UserUpdateDto userDto) {
        User old = userRepository.findById(getAuthenticatedUser().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        Optional.ofNullable(userDto.getPassword()).filter(it -> !it.isEmpty()).map(passwordEncoder::encode).ifPresent(old::setPassword);
        Optional.ofNullable(userDto.getName()).filter(it -> !it.isEmpty()).ifPresent(old::setName);
        Optional.ofNullable(userDto.getCellphone()).filter(it -> !it.isEmpty()).ifPresent(old::setCellphone);

        Optional.ofNullable(userDto.getEmail()).filter(it -> !it.isEmpty()).ifPresent(email -> {
            userRepository.findByEmail(email).ifPresent(byEmail -> {
                if (!byEmail.getId().equals(old.getId()))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
            });

            old.setEmail(email);
        });

        Optional.ofNullable(userDto.getRestrictionIds()).ifPresent(restrictionIds -> {
            for (String id : restrictionIds) {
                restrictionRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTRICTION_NOT_FOUND));
            }
            old.setRestrictionIds(restrictionIds);
        });

        return userRepository.save(old);
    }

    public User updateImage(MultipartFile imageFile) throws IOException {
        User user = getAuthenticatedUser();
        String newBlobName = azureBlobService.uploadMultipartFile(
                imageFile, user.getImage(), "users", user.getId());
        user.setImage(newBlobName);
        return userRepository.save(user);
    }

    public void delete(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !getAuthenticatedUser().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_USER);

        if (!restaurantRepository.findAllByOwnerId(user.getId()).isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete user with restaurants");

        if (user.getImage() != null && !user.getImage().isBlank()) {
            azureBlobService.deleteRelativeBlob(user.getImage());
        }

        paymentRepository.deleteAllByUserId(user.getId());
        addressRepository.deleteAllByUserId(user.getId());
        cartRepository.deleteById(user.getCartId());

        userRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
