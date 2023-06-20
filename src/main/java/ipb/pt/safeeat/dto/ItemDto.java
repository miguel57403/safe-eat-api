package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemDto {
    private String id;
    @NotNull(message = "Invalid product")
    private String productId;
    @NotNull(message = "Invalid quantity")
    private Integer quantity;
}
