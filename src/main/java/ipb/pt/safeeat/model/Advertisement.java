package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "advertisements")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Advertisement {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String title;
    private String image;
    private String restaurantId;
}
