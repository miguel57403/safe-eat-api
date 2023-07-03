package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@NoArgsConstructor
@Document(collection = "items")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private Integer quantity;
    private Double subtotal;
    @DocumentReference
    private Product product;
}
