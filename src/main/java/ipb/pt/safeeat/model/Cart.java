package ipb.pt.safeeat.model;

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
    private Integer quantity = 0;
    private Double subtotal = 0.0;
    @DocumentReference(lazy = true)
    private List<Item> items = new ArrayList<>();
}
