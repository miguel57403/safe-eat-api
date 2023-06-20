package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @NotEmpty(message = "Invalid itemsIds")
    private List<String> itemIds;
    @NotNull(message = "Invalid restaurantId")
    private String restaurantId;
    @NotNull(message = "Invalid clientId")
    private String clientId;
}
