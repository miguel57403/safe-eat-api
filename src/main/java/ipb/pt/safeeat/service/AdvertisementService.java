package ipb.pt.safeeat.service;

import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.dto.AdvertisementDto;
import ipb.pt.safeeat.model.Advertisement;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.repository.AdvertisementRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdvertisementService {
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Advertisement> findAll() {
        return advertisementRepository.findAll();
    }

    public Advertisement findById(String id) {
        return advertisementRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADVERTISEMENT_NOT_FOUND));
    }

    public Advertisement create(AdvertisementDto advertisementDto) {
        Restaurant restaurant = restaurantRepository.findById(advertisementDto.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        Advertisement advertisement = new Advertisement();
        BeanUtils.copyProperties(advertisementDto, advertisement);
        Advertisement created = advertisementRepository.save(advertisement);

        restaurant.getAdvertisements().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    @Transactional
    public List<Advertisement> createMany(List<AdvertisementDto> advertisementDtos) {
        List<Advertisement> created = new ArrayList<>();
        for (AdvertisementDto advertisementDto : advertisementDtos) {
            created.add(create(advertisementDto));
        }

        return created;
    }

    public Advertisement update(AdvertisementDto advertisementDto) {
        Advertisement old = advertisementRepository.findById(advertisementDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADVERTISEMENT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Restaurant> restaurant = restaurantRepository.findByAdvertisements(old);

        if (restaurant.isEmpty() || !restaurant.get().getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND);

        if (!restaurant.get().getAdvertisements().contains(old))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADVERTISEMENT_NOT_FOUND);

        BeanUtils.copyProperties(advertisementDto, old);
        return advertisementRepository.save(old);
    }

    public void delete(String id) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADVERTISEMENT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Restaurant> restaurant = restaurantRepository.findByAdvertisements(advertisement);

        if (restaurant.isEmpty() || !restaurant.get().getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND);


        if (!restaurant.get().getAdvertisements().contains(advertisement))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADVERTISEMENT_NOT_FOUND);

        restaurant.get().getAdvertisements().remove(advertisement);
        restaurantRepository.save(restaurant.get());
        advertisementRepository.deleteById(id);
    }
}
