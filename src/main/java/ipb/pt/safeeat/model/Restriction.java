package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "restrictions")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Restriction {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String description;
    private Boolean isRestricted;
}
