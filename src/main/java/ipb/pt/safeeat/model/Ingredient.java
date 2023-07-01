package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "ingredients")
public class Ingredient {
    @Id
    private String id;
    private String name;
    private String description;
    private Boolean isRestricted;
    @DocumentReference(lazy = true)
    private List<Restriction> restrictions;
}
