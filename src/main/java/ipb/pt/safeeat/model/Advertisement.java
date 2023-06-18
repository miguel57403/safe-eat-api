package ipb.pt.safeeat.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@NoArgsConstructor
@Document(collection = "advertisements")
public class Advertisement {
    @Id
    private String id;
    @NotEmpty(message = "Invalid title")
    private String title;
    @NotEmpty(message = "Invalid image")
    private String image;
    @DocumentReference
    private Restaurant restaurant;
}