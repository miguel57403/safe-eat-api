package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.DeliveryConstants;
import ipb.pt.safeeat.constants.RestaurantConstants;
import ipb.pt.safeeat.model.Delivery;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.repository.DeliveryRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Delivery> getAll() {
        return deliveryRepository.findAll();
    }

    public Delivery findById(String id) {
        return deliveryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, DeliveryConstants.NOT_FOUND));
    }

    public Delivery create(Delivery delivery, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        Delivery created = deliveryRepository.save(delivery);

        restaurant.getDeliveries().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    @Transactional
    public List<Delivery> createMany(List<Delivery> deliveries, String restaurantId) {
        List<Delivery> created = new ArrayList<>();
        for(Delivery delivery : deliveries) {
            created.add(create(delivery, restaurantId));
        }

        return created;
    }

    public Delivery update(Delivery delivery) {
        Delivery old = deliveryRepository.findById(delivery.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, DeliveryConstants.NOT_FOUND));

        BeanUtils.copyProperties(delivery, old);
        return deliveryRepository.save(delivery);
    }

    public void delete(String id) {
        deliveryRepository.deleteById(id);
    }
}
