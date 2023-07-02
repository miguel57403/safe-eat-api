package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "deliveries")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class  Delivery {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private Double price;
    private String startTime;
    private String endTime;
}
