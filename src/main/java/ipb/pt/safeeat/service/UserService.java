package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.UserDto;
import ipb.pt.safeeat.dto.UserUpdateDto;
import ipb.pt.safeeat.model.Cart;
import ipb.pt.safeeat.model.Restriction;
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
import java.io.InputStream;
import java.util.ArrayList;
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

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        User current = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!current.isAdmin() && !current.equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_USER);

        return user;
    }

    public User create(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");

        List<Restriction> restrictions = getRestrictions(userDto.getRestrictionIds());

        User user = new User();
        BeanUtils.copyProperties(userDto, user);

        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRestrictions(restrictions);

        Cart cart = cartRepository.save(new Cart());
        user.setCart(cart);

        return userRepository.save(user);
    }

    private List<Restriction> getRestrictions(List<String> restrictionIds) {
        List<Restriction> restrictions = new ArrayList<>();
        if (!restrictionIds.isEmpty()) {
            for (String restrictionId : restrictionIds) {
                restrictions.add(restrictionRepository.findById(restrictionId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTRICTION_NOT_FOUND)));
            }
        }
        return restrictions;
    }

    public User update(UserUpdateDto userDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User updating = userRepository.findById(user.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        Optional.ofNullable(userDto.getPassword()).filter(it -> !it.isEmpty()).ifPresent(password -> {
            updating.setPassword(passwordEncoder.encode(password));
        });

        Optional.ofNullable(userDto.getName()).filter(it -> !it.isEmpty()).ifPresent(name -> {
            updating.setName(name);
        });

        Optional.ofNullable(userDto.getEmail()).filter(it -> !it.isEmpty()).ifPresent(email -> {
            userRepository.findByEmail(email).ifPresent(byEmail -> {
                if (!byEmail.getId().equals(updating.getId()))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
            });

            updating.setEmail(email);
        });

        Optional.ofNullable(userDto.getRestrictionIds()).ifPresent(restrictionsIds -> {
            List<Restriction> restrictions = getRestrictions(restrictionsIds);
            updating.setRestrictions(restrictions);
        });

        return userRepository.save(updating);
    }

    public User updateImage(MultipartFile imageFile) throws IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        InputStream imageStream = imageFile.getInputStream();
        String blobName = imageFile.getOriginalFilename();

        if (blobName == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is null");

        if (user.getImage() != null && !user.getImage().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(user.getImage().replace(containerUrl, ""));
        }

        String extension = blobName.substring(blobName.lastIndexOf(".") + 1);
        String partialBlobName = "users/" + user.getId() + "." + extension;
        azureBlobService.uploadBlob(partialBlobName, imageStream);

        String newBlobName = azureBlobService.getBlobUrl(partialBlobName);
        user.setImage(newBlobName);
        return userRepository.save(user);
    }

    public void delete(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        User current = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!current.isAdmin() && !current.equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_USER);

        if (!user.getRestaurants().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete user with restaurants");

        if (user.getImage() != null && !user.getImage().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(user.getImage().replace(containerUrl, ""));
        }

        paymentRepository.deleteAll(user.getPayments());
        addressRepository.deleteAll(user.getAddresses());
        cartRepository.delete(user.getCart());

        userRepository.deleteById(id);
    }
}
