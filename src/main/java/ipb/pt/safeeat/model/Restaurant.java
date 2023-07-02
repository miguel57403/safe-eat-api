package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "restaurants")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Restaurant {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String logo;
    private String cover;
    @DocumentReference(lazy = true)
    private List<Delivery> deliveries = new ArrayList<>();

    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Product> products = new ArrayList<>();
    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<ProductSection> productSections = new ArrayList<>();
    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Advertisement> advertisements = new ArrayList<>();
    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Ingredient> ingredients = new ArrayList<>();
    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Order> orders = new ArrayList<>();
    @JsonIgnore
    @DocumentReference(lazy = true)
    private User owner;
}
