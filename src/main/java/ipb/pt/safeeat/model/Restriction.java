package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@Document(collection = "restrictions")
public class Restriction {
    @Id
    private String id;
    @NotEmpty(message = "Invalid name")
    private String name;
    @NotEmpty(message = "Invalid description")
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isRestricted;
}