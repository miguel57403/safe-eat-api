package ipb.pt.safeeat.service;

import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.dto.AddressDto;
import ipb.pt.safeeat.model.Address;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.AddressRepository;
import ipb.pt.safeeat.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Address> getAll() {
        return addressRepository.findAll();
    }

    public Address findById(String id) {
        return addressRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADDRESS_NOT_FOUND));
    }

    public List<Address> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        return user.getAddress();
    }

    public Address create(AddressDto addressDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADDRESS_NOT_FOUND));

        Address address = new Address();
        BeanUtils.copyProperties(addressDto, address);
        Address created = addressRepository.save(address);

        user.getAddress().add(created);
        userRepository.save(user);

        return created;
    }

    @Transactional
    public List<Address> createMany(List<AddressDto> addressDtos, String userId) {
        List<Address> created = new ArrayList<>();
        for (AddressDto addressDto : addressDtos) {
            created.add(create(addressDto, userId));
        }

        return created;
    }

    public Address update(AddressDto addressDto) {
        Address old = addressRepository.findById(addressDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADDRESS_NOT_FOUND));

        BeanUtils.copyProperties(addressDto, old);
        return addressRepository.save(old);
    }

    public void delete(String id, String userId) {
        Address address = addressRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADDRESS_NOT_FOUND));

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            user.get().getAddress().remove(address);
            userRepository.save(user.get());
        }

        addressRepository.deleteById(id);
    }
}
