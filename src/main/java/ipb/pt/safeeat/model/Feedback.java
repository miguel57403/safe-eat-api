package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "feedbacks")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class  Feedback {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private Integer rating;
    private String comment;
}
