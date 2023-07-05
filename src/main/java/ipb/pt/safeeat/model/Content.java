package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Content {
    private Advertisement advertisement;
    private RestaurantSection restaurantSection;

    public static Content withAdvertisement(Advertisement advertisement) {
        Content content = new Content();
        content.setAdvertisement(advertisement);
        return content;
    }

    public static Content withRestaurantSection(RestaurantSection restaurantSection) {
        Content content = new Content();
        content.setRestaurantSection(restaurantSection);
        return content;
    }
}
