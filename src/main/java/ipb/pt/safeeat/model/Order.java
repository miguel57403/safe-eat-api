package ipb.pt.safeeat.model;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order )) return false;
        return id != null && id.equals(((Order) o).getId());
    }
}
