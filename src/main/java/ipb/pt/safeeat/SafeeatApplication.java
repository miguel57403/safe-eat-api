package ipb.pt.safeeat;

import ipb.pt.safeeat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SafeeatApplication implements CommandLineRunner {
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    AdvertisementRepository advertisementRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    DeliveryRepository repository;
    @Autowired
    FeedbackRepository feedbackRepository;
    @Autowired
    IngredientRepository ingredientRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    OrderRepository

    public static void main(String[] args) {
        SpringApplication.run(SafeeatApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
