package ipb.pt.safeeat.model;

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
@Document(collection = "ingredients")
public class Ingredient {
    @Id
    private String id;
    @NotEmpty(message = "Invalid name")
    private String name;
    @NotEmpty(message = "Invalid description")
    private String description;
    @DocumentReference
    private List<Restriction> restrictions = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isRestricted;
}
