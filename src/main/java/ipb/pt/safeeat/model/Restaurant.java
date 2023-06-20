package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "restaurants")
public class Restaurant {
    @Id
    private String id;
    private String name;
    private String logo;
    private String cover;
    private List<Delivery> deliveries;

    @DocumentReference
    @JsonIgnore
    private List<Product> products;
    @DocumentReference
    @JsonIgnore
    private List<ProductSection> productSections;
    @DocumentReference
    @JsonIgnore
    private List<Advertisement> advertisements;
    @DocumentReference
    @JsonIgnore
    private List<Ingredient> ingredients;
    @DocumentReference
    @JsonIgnore
    private List<Order> orders;
    @JsonIgnore
    @DocumentReference
    private User owner;
}
