package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductDto {
    private String id;
    @NotEmpty(message = "Invalid name")
    private String name;
    @NotNull(message = "Invalid price")
    private Double price;
    @NotEmpty(message = "Invalid categoryId")
    private String categoryId;
    @NotEmpty(message = "Invalid ingredientIds")
    private List<String> ingredientIds;
}
