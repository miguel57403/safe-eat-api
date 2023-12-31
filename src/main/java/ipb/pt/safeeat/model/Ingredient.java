package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "ingredients")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ingredient {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String description;
    private Boolean isRestricted;
    private String restaurantId;
    @JsonIgnore
    private List<String> restrictionIds;
}
