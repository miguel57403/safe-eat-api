package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "carts")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cart {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private Integer quantity = 0;
    private Double subtotal = 0.0;
    private List<Item> items = new ArrayList<>();
    private String restaurantId;
    @JsonIgnore
    private String selectedDelivery;
}
