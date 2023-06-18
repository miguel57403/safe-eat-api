package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.AddressConstants;
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, AddressConstants.NOT_FOUND));
    }

    public Address create(Address address, String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, AddressConstants.NOT_FOUND));

        Address created = addressRepository.save(address);

        user.getAddress().add(created);
        userRepository.save(user);

        return created;
    }

    @Transactional
    public List<Address> createMany(List<Address> addresses, String userId) {
        List<Address> created = new ArrayList<>();
        for(Address address : addresses) {
            created.add(create(address, userId));
        }

        return created;
    }

    public Address update(Address address) {
        Address old = addressRepository.findById(address.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, AddressConstants.NOT_FOUND));

        BeanUtils.copyProperties(address, old);
        return addressRepository.save(address);
    }

    public void delete(String id) {
        addressRepository.deleteById(id);
    }
}
