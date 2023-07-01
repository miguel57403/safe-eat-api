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
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private Double price;
    private String image;
    private Boolean isRestricted;
    @JsonIgnore
    @DocumentReference(lazy = true)
    private Category category;
    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Ingredient> ingredients;
}
