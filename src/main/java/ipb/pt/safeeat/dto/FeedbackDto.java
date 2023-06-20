package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FeedbackDto {
    private String id;
    @NotNull(message = "Invalid rating")
    private Integer rating;
    @NotEmpty(message = "Invalid comment")
    private String comment;
}
