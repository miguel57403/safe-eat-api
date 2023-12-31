package ipb.pt.safeeat.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "orders")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String status;
    private LocalDateTime time;
    private LocalDateTime registeredAt;
    private LocalDateTime preparingAt;
    private LocalDateTime transportingAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime canceledAt;
    private Double subtotal;
    private Double total;
    private Integer quantity;
    private Address address;
    private Payment payment;
    private Delivery delivery;
    private List<Item> items;
    private String feedbackId;
    @DocumentReference
    private Restaurant restaurant;
    @DocumentReference
    private User client;
}
