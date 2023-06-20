package ipb.pt.safeeat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {
    private String id;
    private String password;
    @NotEmpty(message = "Invalid image")
    private String image;
    @NotEmpty(message = "Invalid name")
    private String name;
    @Email(message = "Invalid email")
    private String email;
    @NotEmpty(message = "Invalid cellphone")
    private String cellphone;
    @NotNull(message = "Invalid restrictionIds")
    private List<String> restrictionIds;
}
