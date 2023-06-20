package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdvertisementDto {
    private String id;
    @NotEmpty(message = "Invalid title")
    private String title;
    @NotEmpty(message = "Invalid image")
    private String image;
    @NotEmpty(message = "Invalid restaurantId")
    private String restaurantId;
}
