package ipb.pt.safeeat.service;

import ipb.pt.safeeat.dto.AdvertisementDto;
import ipb.pt.safeeat.model.Advertisement;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.AdvertisementRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import ipb.pt.safeeat.utility.ForbiddenConstants;
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
public class AdvertisementService {
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Advertisement> findAll() {
        return advertisementRepository.findAll();
    }

    public Advertisement findById(String id) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADVERTISEMENT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findByAdvertisements(advertisement).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ADVERTISEMENT);

        return advertisement;
    }

    public List<Advertisement> findAllByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ADVERTISEMENT);

        return restaurant.getAdvertisements();
    }

    public Advertisement create(AdvertisementDto advertisementDto) {
        Restaurant restaurant = restaurantRepository.findById(advertisementDto.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ADVERTISEMENT);

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
        Restaurant restaurant = restaurantRepository.findByAdvertisements(old).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ADVERTISEMENT);

        if (!restaurant.getAdvertisements().contains(old))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ADVERTISEMENT);

        BeanUtils.copyProperties(advertisementDto, old);
        return advertisementRepository.save(old);
    }

    public void delete(String id) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.ADVERTISEMENT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Restaurant restaurant = restaurantRepository.findByAdvertisements(advertisement).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ADVERTISEMENT);


        if (!restaurant.getAdvertisements().contains(advertisement))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstants.FORBIDDEN_ADVERTISEMENT);

        restaurant.getAdvertisements().remove(advertisement);
        restaurantRepository.save(restaurant);

        advertisementRepository.deleteById(id);
    }
}
