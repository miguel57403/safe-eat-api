package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "notifications")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String content;
    private LocalDateTime time;
    private Boolean isViewed;
    @DocumentReference(lazy = true)
    private Order order;
}
