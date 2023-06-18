package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.AdvertisementConstants;
import ipb.pt.safeeat.constants.RestaurantConstants;
import ipb.pt.safeeat.model.Advertisement;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.repository.AdvertisementRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public List<Advertisement> getAll() {
        return advertisementRepository.findAll();
    }

    public Advertisement findById(String id) {
        return advertisementRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, AdvertisementConstants.NOT_FOUND));
    }

    public Advertisement create(Advertisement advertisement) {
        if (advertisement.getRestaurant() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant is required");

        Restaurant restaurant = restaurantRepository.findById(advertisement.getRestaurant().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        advertisement.setRestaurant(restaurant);
        Advertisement created = advertisementRepository.save(advertisement);

        restaurant.getAdvertisements().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    @Transactional
    public List<Advertisement> createMany(List<Advertisement> advertisements) {
        List<Advertisement> created = new ArrayList<>();
        for (Advertisement advertisement : advertisements) {
            created.add(create(advertisement));
        }

        return created;
    }

    public Advertisement update(Advertisement advertisement) {
        Advertisement old = advertisementRepository.findById(advertisement.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, AdvertisementConstants.NOT_FOUND));

        old.setTitle(advertisement.getTitle());
        old.setImage(advertisement.getImage());

        return advertisementRepository.save(old);
    }

    public void delete(String id) {
        advertisementRepository.deleteById(id);
    }
}

