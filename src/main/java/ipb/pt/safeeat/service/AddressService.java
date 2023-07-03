package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.AddressDto;
import ipb.pt.safeeat.model.Address;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.AddressRepository;
import ipb.pt.safeeat.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    public Address findById(String id) {
        Address address = addressRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ADDRESS_NOT_FOUND));

        User user = getAuthenticatedUser();
        List<Address> userAddresses = addressRepository.findAllByUserId(user.getId());

        if (!user.isAdmin() && !userAddresses.contains(address))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADDRESS);

        return address;
    }

    public List<Address> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        if (!getAuthenticatedUser().isAdmin() && !getAuthenticatedUser().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADDRESS);

        return addressRepository.findAllByUserId(user.getId());
    }

    public List<Address> findMe() {
        return addressRepository.findAllByUserId(getAuthenticatedUser().getId());
    }

    public Address create(AddressDto addressDto) {
        User user = getAuthenticatedUser();

        Address address = new Address();
        BeanUtils.copyProperties(addressDto, address);
        address.setUserId(user.getId());

        return addressRepository.save(address);
    }

    public Address update(AddressDto addressDto) {
        Address old = addressRepository.findById(addressDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ADDRESS_NOT_FOUND));

        List<Address> userAddresses = addressRepository.findAllByUserId(getAuthenticatedUser().getId());

        if (!userAddresses.contains(old))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADDRESS);

        BeanUtils.copyProperties(addressDto, old);
        return addressRepository.save(old);
    }

    public Address select(String id) {
        List<Address> userAddresses = addressRepository.findAllByUserId(getAuthenticatedUser().getId());

        Address selectedAddress = userAddresses.stream()
                .filter(address -> address.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ADDRESS_NOT_FOUND));

        selectedAddress.setIsSelected(true);
        addressRepository.save(selectedAddress);

        return selectedAddress;
    }

    public void delete(String id) {
        Address address = addressRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ADDRESS_NOT_FOUND));

        List<Address> userAddresses = addressRepository.findAllByUserId(getAuthenticatedUser().getId());

        if (!userAddresses.contains(address))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADDRESS);

        addressRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
