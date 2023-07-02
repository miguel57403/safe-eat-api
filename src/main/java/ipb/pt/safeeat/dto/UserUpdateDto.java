package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserUpdateDto {
    private String password;
    private String name;
    @Email(message = "Invalid email")
    private String email;
    private String cellphone;
    private List<String> restrictionIds;
}
