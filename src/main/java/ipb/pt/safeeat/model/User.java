package ipb.pt.safeeat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    @JsonIgnore
    private String password;
    private String image;
    private String name;
    private String email;
    private String cellphone;
    @JsonIgnore
    private String role;

    @JsonIgnore
    @DocumentReference(lazy = true)
    private Cart cart;
    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Restriction> restrictions = new ArrayList<>();
    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Payment> payments = new ArrayList<>();
    @JsonIgnore
    @DocumentReference(lazy = true)
    private List<Address> addresses = new ArrayList<>();

    @JsonIgnore
    public Boolean isAdmin() {
        return role.equals("ADMIN");
    }

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
