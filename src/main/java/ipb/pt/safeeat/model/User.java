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

    @JsonIgnore
    @DocumentReference
    private Cart cart;
    @JsonIgnore
    @DocumentReference
    private List<Restriction> restrictions;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
