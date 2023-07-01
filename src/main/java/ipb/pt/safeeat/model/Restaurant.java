package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "restaurants")
public class Restaurant {
    @Id
    private String id;
    private String name;
    private String logo;
    private String cover;
    @DocumentReference(lazy = true)
    private List<Delivery> deliveries = new ArrayList<>();

    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Notification> notifications = new ArrayList<>();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant )) return false;
        return id != null && id.equals(((Restaurant) o).getId());
    }
}
