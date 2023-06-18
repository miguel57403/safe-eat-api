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
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer quantity = 0;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double subtotal = 0.0;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @DocumentReference
    private List<Item> items = new ArrayList<>();
}