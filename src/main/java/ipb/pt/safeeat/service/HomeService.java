package ipb.pt.safeeat.service;

import ipb.pt.safeeat.model.Advertisement;
import ipb.pt.safeeat.model.Content;
import ipb.pt.safeeat.model.Home;
import ipb.pt.safeeat.model.RestaurantSection;
import ipb.pt.safeeat.repository.AdvertisementRepository;
import ipb.pt.safeeat.repository.RestaurantSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HomeService {
    @Autowired
    private RestaurantSectionRepository restaurantSectionRepository;
    @Autowired
    private AdvertisementRepository advertisementRepository;

    public Home findOne() {
        List<RestaurantSection> restaurantSections = restaurantSectionRepository.findAll();
        List<Advertisement> advertisements = advertisementRepository.findAll();

        List<Content> contentList = new ArrayList<>();

        for (RestaurantSection restaurantSection : restaurantSections) {
            Content content = new Content();
            content.setRestaurantSection(restaurantSection);
            contentList.add(content);
        }

        for (Advertisement advertisement : advertisements) {
            Content content = new Content();
            content.setAdvertisement(advertisement);
            contentList.add(content);
        }

        Collections.shuffle(contentList);
        return new Home(contentList);
    }
}
