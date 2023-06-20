package ipb.pt.safeeat.model;

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
    private String status;
    private LocalDateTime time;
    private Double subtotal;
    private Double total;
    private Integer quantity;
    private Address address;
    private Payment payment;
    private Delivery delivery;
    private List<Item> items;
    @DocumentReference
    private Restaurant restaurant;
    @DocumentReference
    private Feedback feedback;
    @DocumentReference
    private User client;
}
