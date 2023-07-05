package ipb.pt.safeeat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartIsEmptyResponseDto {
    private Boolean isEmpty;
    private String restaurantId;
}
