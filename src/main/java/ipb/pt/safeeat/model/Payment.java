package ipb.pt.safeeat.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.YearMonth;

@Data
@NoArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    @NotEmpty(message = "Invalid type")
    private String type;
    @NotEmpty(message = "Invalid name")
    private String name;
    @NotNull(message = "Invalid number")
    private Integer number;
    @NotEmpty(message = "Invalid expirationDate")
    private String expirationDate;
    @NotNull(message = "Invalid ccv")
    private Integer cvv;
}