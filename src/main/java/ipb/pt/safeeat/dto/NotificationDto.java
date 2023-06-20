package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationDto {
    private String id;
    @NotEmpty(message = "Invalid content")
    private String content;
    @NotNull(message = "Invalid order")
    private String orderId;
}
