package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.RestaurantSectionDto;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.RestaurantSection;
import ipb.pt.safeeat.repository.RestaurantRepository;
import ipb.pt.safeeat.repository.RestaurantSectionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantSectionService {
    @Autowired
    private RestaurantSectionRepository restaurantSectionRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<RestaurantSection> findAll() {
        return restaurantSectionRepository.findAll();
    }

    public RestaurantSection findById(String id) {
        return restaurantSectionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_SECTION_NOT_FOUND));
    }

    public RestaurantSection create(RestaurantSectionDto restaurantSectionDto) {
        List<Restaurant> restaurants = new ArrayList<>();
        for (String restaurantId : restaurantSectionDto.getRestaurantIds()) {
            restaurants.add(restaurantRepository.findById(restaurantId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND)));
        }

        RestaurantSection restaurantSection = new RestaurantSection();
        BeanUtils.copyProperties(restaurantSectionDto, restaurantSection);

        restaurantSection.setRestaurants(restaurants);
        return restaurantSectionRepository.save(restaurantSection);
    }

    public RestaurantSection update(RestaurantSectionDto restaurantSectionDto) {
        RestaurantSection old = restaurantSectionRepository.findById(restaurantSectionDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_SECTION_NOT_FOUND));

        BeanUtils.copyProperties(restaurantSectionDto, old);
        return restaurantSectionRepository.save(old);
    }

    public void delete(String id) {
        restaurantSectionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_SECTION_NOT_FOUND));

        restaurantSectionRepository.deleteById(id);
    }
}
