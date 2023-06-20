package ipb.pt.safeeat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "addresses")
public class Address {
    @Id
    private String id;
    private String name;
    private String street;
    private String number;
    private String complement;
    private String city;
    private String postalCode;
}
