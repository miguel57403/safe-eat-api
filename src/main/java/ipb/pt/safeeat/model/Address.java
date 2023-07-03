package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "addresses")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String street;
    private String number;
    private String complement;
    private String city;
    private String postalCode;
    private Boolean isSelected = false;
    @JsonIgnore
    private String userId;
}
