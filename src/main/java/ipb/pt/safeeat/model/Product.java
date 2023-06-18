package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotEmpty(message = "Invalid name")
    private String name;
    @NotNull(message = "Invalid price")
    private Double price;
    @NotNull(message = "Invalid images")
    private List<String> images;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isRestricted;
    @DocumentReference
    @NotEmpty(message = "Invalid categories")
    private List<Category> categories;
    @DocumentReference
    @NotEmpty(message = "Invalid ingredients")
    private List<Ingredient> ingredients;
}