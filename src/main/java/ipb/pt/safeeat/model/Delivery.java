package ipb.pt.safeeat.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@Document(collection = "deliveries")
public class Delivery {
    @Id
    private String id;
    @NotEmpty(message = "Invalid name")
    private String name;
    @NotNull(message = "Invalid price")
    private Double price;
    @NotEmpty(message = "Invalid startTime")
    private String startTime;
    @NotEmpty(message = "Invalid endTime")
    private String endTime;
}
