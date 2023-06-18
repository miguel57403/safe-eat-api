package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "restaurants")
public class Restaurant {
    @Id
    private String id;
    @NotEmpty(message = "Invalid name")
    private String name;
    @NotEmpty(message = "Invalid logo")
    private String logo;
    @NotEmpty(message = "Invalid cover")
    private String cover;
    @DocumentReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Delivery> deliveries = new ArrayList<>();
    @DocumentReference
    @JsonIgnore
    private List<Product> products = new ArrayList<>();
    @DocumentReference
    @JsonIgnore
    private List<ProductSection> productSections = new ArrayList<>();
    @DocumentReference
    @JsonIgnore
    private List<Advertisement> advertisements = new ArrayList<>();
    @DocumentReference
    @JsonIgnore
    private List<Ingredient> ingredients = new ArrayList<>();
    @DocumentReference
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();
    @DocumentReference
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User owner;
}