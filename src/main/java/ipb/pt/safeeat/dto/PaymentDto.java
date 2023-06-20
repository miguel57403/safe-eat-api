package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentDto {
    private String id;
    @NotEmpty(message = "Invalid type")
    private String type;
    @NotEmpty(message = "Invalid name")
    private String name;
    @NotNull(message = "Invalid number")
    private Integer number;
    @NotEmpty(message = "Invalid expirationDate")
    private String expirationDate;
    @NotNull(message = "Invalid ccv")
    private Integer cvv;
}
