package ipb.pt.safeeat.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "feedbacks")
public class Feedback {
    @Id
    private String id;
    @NotNull(message = "Invalid rating")
    private Integer rating;
    @NotEmpty(message = "Invalid comment")
    private String comment;
}
