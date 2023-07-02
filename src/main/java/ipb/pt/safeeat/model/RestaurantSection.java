package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "restaurantSections")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RestaurantSection {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    @DocumentReference(lazy = true)
    private List<Restaurant> restaurants;
}
