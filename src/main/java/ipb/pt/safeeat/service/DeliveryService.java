package ipb.pt.safeeat.service;

import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.dto.DeliveryDto;
import ipb.pt.safeeat.model.Delivery;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.repository.DeliveryRepository;
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
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Delivery> findAll() {
        return deliveryRepository.findAll();
    }

    public Delivery findById(String id) {
        return deliveryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.DELIVERY_NOT_FOUND));
    }

    public Delivery create(DeliveryDto deliveryDto, String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        Delivery delivery = new Delivery();
        BeanUtils.copyProperties(deliveryDto, delivery);
        Delivery created = deliveryRepository.save(delivery);

        restaurant.getDeliveries().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    @Transactional
    public List<Delivery> createMany(List<DeliveryDto> deliveryDtos, String restaurantId) {
        List<Delivery> created = new ArrayList<>();
        for (DeliveryDto deliveryDto : deliveryDtos) {
            created.add(create(deliveryDto, restaurantId));
        }

        return created;
    }

    public Delivery update(DeliveryDto deliveryDto) {
        Delivery old = deliveryRepository.findById(deliveryDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.DELIVERY_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Restaurant> restaurant = restaurantRepository.findByDeliveries(old);

        if (restaurant.isEmpty() || !restaurant.get().getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND);

        if (!restaurant.get().getDeliveries().contains(old))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.DELIVERY_NOT_FOUND);

        BeanUtils.copyProperties(deliveryDto, old);
        return deliveryRepository.save(old);
    }

    public void delete(String id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.DELIVERY_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Restaurant> restaurant = restaurantRepository.findByDeliveries(delivery);

        if (restaurant.isEmpty() || !restaurant.get().getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND);

        if (!restaurant.get().getDeliveries().contains(delivery))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.DELIVERY_NOT_FOUND);

        restaurant.get().getDeliveries().remove(delivery);
        restaurantRepository.save(restaurant.get());
        deliveryRepository.deleteById(id);
    }
}
