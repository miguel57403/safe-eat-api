package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.*;
import ipb.pt.safeeat.model.*;
import ipb.pt.safeeat.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Restaurant> getAll() {
        try {
            return restaurantRepository.findAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public Restaurant findById(String id) {
        return restaurantRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));
    }

    public List<Restaurant> findByOwner(String ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstants.NOT_FOUND));

        List<Restaurant> restaurants = new ArrayList<>();
        for(Restaurant restaurant : owner.getRestaurants()){
            restaurants.add(restaurantRepository.findById(restaurant.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND)));
        }

        return restaurants;
    }

    public Restaurant create(Restaurant restaurant) {
        if(restaurant.getOwner() == null || restaurant.getOwner().getId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner is required");

        User owner = userRepository.findById(restaurant.getOwner().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstants.NOT_FOUND));

        Restaurant created = restaurantRepository.save(restaurant);

        owner.getRestaurants().add(created);
        userRepository.save(owner);

        return created;
    }

    @Transactional
    public List<Restaurant> createMany(List<Restaurant> restaurants) {
        List<Restaurant> created = new ArrayList<>();
        for(Restaurant restaurant : restaurants) {
            created.add(create(restaurant));
        }

        return created;
    }

    public Restaurant update(Restaurant restaurant) {
        Restaurant old = restaurantRepository.findById(restaurant.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        BeanUtils.copyProperties(restaurant, old);
        return restaurantRepository.save(restaurant);
    }

    public void delete(String id) {
        restaurantRepository.deleteById(id);
    }
}