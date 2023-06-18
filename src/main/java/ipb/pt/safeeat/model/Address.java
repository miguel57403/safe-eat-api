package ipb.pt.safeeat.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotEmpty(message = "Invalid name")
    private String name;
    @NotNull(message = "Invalid isDefault")
    private Boolean isDefault;
    @NotEmpty(message = "Invalid street")
    private String street;
    @NotEmpty(message = "Invalid number")
    private String number;
    @NotEmpty(message = "Invalid complement")
    private String complement;
    @NotEmpty(message = "Invalid city")
    private String city;
    @NotEmpty(message = "Invalid postalCode")
    private String postalCode;
}