package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "productSections")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductSection {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    @DocumentReference(lazy = true)
    private List<Product> products;
    @JsonIgnore
    private String restaurantId;
}
