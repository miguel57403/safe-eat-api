package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "payments")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String type;
    private String name;
    private Integer number;
    private String expirationDate;
    private Integer cvv;
}
