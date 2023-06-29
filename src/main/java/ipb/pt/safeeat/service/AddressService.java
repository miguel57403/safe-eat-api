package ipb.pt.safeeat.service;

import ipb.pt.safeeat.dto.AddressDto;
import ipb.pt.safeeat.model.Address;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.AddressRepository;
import ipb.pt.safeeat.repository.UserRepository;
import ipb.pt.safeeat.utility.NotAllowedConstants;
import ipb.pt.safeeat.utility.NotFoundConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    public Address findById(String id) {
        Address address = addressRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADDRESS_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !user.getAddresses().contains(address))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NotAllowedConstants.FORBIDDEN_ADDRESS);

        return address;
    }

    public List<Address> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        return user.getAddresses();
    }

    public Address create(AddressDto addressDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Address address = new Address();
        BeanUtils.copyProperties(addressDto, address);
        Address created = addressRepository.save(address);

        user.getAddresses().add(created);
        userRepository.save(user);

        return created;
    }

    @Transactional
    public List<Address> createMany(List<AddressDto> addressDtos) {
        List<Address> created = new ArrayList<>();
        for (AddressDto addressDto : addressDtos) {
            created.add(create(addressDto));
        }

        return created;
    }

    public Address update(AddressDto addressDto) {
        Address old = addressRepository.findById(addressDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADDRESS_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getAddresses().contains(old))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADDRESS_NOT_FOUND);

        BeanUtils.copyProperties(addressDto, old);
        return addressRepository.save(old);
    }

    public void delete(String id) {
        Address address = addressRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADDRESS_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getAddresses().contains(address))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADDRESS_NOT_FOUND);

        user.getAddresses().remove(address);
        userRepository.save(user);
        addressRepository.deleteById(id);
    }
}
