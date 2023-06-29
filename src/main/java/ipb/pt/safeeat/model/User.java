package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String id;
    @JsonIgnore
    private String password;
    private String image;
    private String name;
    private String email;
    private String cellphone;
    private String role;

    @JsonIgnore
    @DocumentReference
    private Cart cart;
    @JsonIgnore
    @DocumentReference
    private List<Restriction> restrictions = new ArrayList<>();
    @JsonIgnore
    @DocumentReference
    private List<Payment> payments = new ArrayList<>();
    @JsonIgnore
    @DocumentReference
    private List<Address> addresses = new ArrayList<>();
    @JsonIgnore
    @DocumentReference
    private List<Order> orders = new ArrayList<>();
    @JsonIgnore
    @DocumentReference
    private List<Notification> notifications = new ArrayList<>();
    @JsonIgnore
    @DocumentReference
    private List<Restaurant> restaurants = new ArrayList<>();

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
