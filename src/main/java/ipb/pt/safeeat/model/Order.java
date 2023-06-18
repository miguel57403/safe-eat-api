package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime time;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double subtotal = 0.0;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double total = 0.0;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer quantity = 0;
    @NotNull(message = "Invalid address")
    private Address address;
    @NotNull(message = "Invalid payment")
    private Payment payment;
    @NotNull(message = "Invalid delivery")
    private Delivery delivery;
    @NotEmpty(message = "Invalid items")
    private List<Item> items;
    @NotNull(message = "Invalid restaurant")
    @DocumentReference
    private Restaurant restaurant;
    @DocumentReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Feedback feedback;
    @DocumentReference
    @NotNull(message = "Invalid client")
    private User client;
}