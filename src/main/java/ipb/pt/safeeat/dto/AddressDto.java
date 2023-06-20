package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressDto {
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
