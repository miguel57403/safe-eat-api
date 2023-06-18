package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@NoArgsConstructor
@Document(collection = "items")
public class Item {
    @Id
    private String id;
    @DocumentReference
    @NotNull(message = "Invalid product")
    private Product product;
    @NotNull(message = "Invalid quantity")
    private Integer quantity = 0;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double subtotal = 0.0;
}