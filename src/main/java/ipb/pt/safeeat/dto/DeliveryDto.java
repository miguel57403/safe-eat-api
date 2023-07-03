package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeliveryDto {
    private String id;
    @NotEmpty(message = "Invalid name")
    private String name;
    @NotNull(message = "Invalid price")
    private Double price;
    @NotEmpty(message = "Invalid minimum time")
    private String minimumTime;
    @NotEmpty(message = "Invalid maximum time")
    private String maximumTime;
}
