package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "homes")
public class Home {
    @Id
    private String id;
    @DocumentReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Advertisement> advertisements = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<RestaurantSection> restaurantSections = new ArrayList<>();
}