package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDto {
    private String id;
    @NotNull(message = "Invalid addressId")
    private String addressId;
    @NotNull(message = "Invalid paymentId")
    private String paymentId;
    @NotNull(message = "Invalid deliveryId")
    private String deliveryId;
}
