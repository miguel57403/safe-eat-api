package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestaurantDto {
    private String id;
    @NotEmpty(message = "Invalid name")
    private String name;
}
