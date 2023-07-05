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
import java.util.List;

@Service
public class HomeService {
    @Autowired
    private RestaurantSectionRepository restaurantSectionRepository;
    @Autowired
    private AdvertisementRepository advertisementRepository;

    public Home findOne() {
        List<RestaurantSection> restaurantSections = restaurantSectionRepository.findRandomRestaurantSections();
        List<Advertisement> advertisements = advertisementRepository.findRandomAdvertisements();

        List<Content> contentList = new ArrayList<>();

        int indexSection = 0;
        int indexAd = 0;
        while (true) {
            if (indexSection == restaurantSections.size()) break;
            contentList.add(Content.withRestaurantSection(restaurantSections.get(indexSection++)));
            if (indexSection == restaurantSections.size()) break;
            contentList.add(Content.withRestaurantSection(restaurantSections.get(indexSection++)));
            contentList.add(Content.withAdvertisement(advertisements.get(indexAd++)));
            if (indexAd == advertisements.size()) break;
        }

        return new Home(contentList);
    }
}
