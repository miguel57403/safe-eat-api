package ipb.pt.safeeat.model;

import lombok.Data;

@Data
public class Login {
    private String status;
    private String message;
    private String token;
}
