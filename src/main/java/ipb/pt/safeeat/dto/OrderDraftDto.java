package ipb.pt.safeeat.dto;

import ipb.pt.safeeat.model.Address;
import ipb.pt.safeeat.model.Delivery;
import ipb.pt.safeeat.model.Payment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrderDraftDto {
    private List<Address> addresses;
    private List<Delivery> deliveries;
    private List<Payment> payments;
    private Double subtotal;
    private Integer quantity;
}
