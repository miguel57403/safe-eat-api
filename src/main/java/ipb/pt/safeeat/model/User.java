package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    @JsonIgnore
    private String password;
    private String image;
    private String name;
    private String email;
    private String cellphone;
    private List<String> restrictionIds;

    @JsonIgnore
    @DocumentReference
    private Cart cart;
    @JsonIgnore
    @DocumentReference
    private List<Payment> payments;
    @JsonIgnore
    @DocumentReference
    private List<Address> address;
    @JsonIgnore
    @DocumentReference
    private List<Order> orders;
    @JsonIgnore
    @DocumentReference
    private List<Notification> notifications;
    @JsonIgnore
    @DocumentReference
    private List<Restaurant> restaurants;
}
