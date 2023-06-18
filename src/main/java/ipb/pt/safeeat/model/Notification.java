package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    @NotEmpty(message = "Invalid content")
    private String content;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime time;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isViewed = false;
    @NotNull(message = "Invalid order")
    @DocumentReference
    private Order order;
}