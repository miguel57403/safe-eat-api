package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.ExceptionConstants;
import ipb.pt.safeeat.dto.AdvertisementDto;
import ipb.pt.safeeat.model.Advertisement;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.repository.AdvertisementRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
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
public class AdvertisementService {
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Advertisement> getAll() {
        return advertisementRepository.findAll();
    }

    public Advertisement findById(String id) {
        return advertisementRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.ADVERTISEMENT_NOT_FOUND));
    }

    public Advertisement create(AdvertisementDto advertisementDto) {
        Restaurant restaurant = restaurantRepository.findById(advertisementDto.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.RESTAURANT_NOT_FOUND));

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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.ADVERTISEMENT_NOT_FOUND));

        if (!advertisementDto.getRestaurantId().equals(old.getRestaurantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change restaurant");
        }

        BeanUtils.copyProperties(advertisementDto, old);
        return advertisementRepository.save(old);
    }

    public void delete(String id, String restaurantId) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ExceptionConstants.ADVERTISEMENT_NOT_FOUND));

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);

        if (restaurant.isPresent()) {
            restaurant.get().getAdvertisements().remove(advertisement);
            restaurantRepository.save(restaurant.get());
        }

        advertisementRepository.deleteById(id);
    }
}
