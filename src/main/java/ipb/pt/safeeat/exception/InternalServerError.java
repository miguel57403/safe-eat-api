package ipb.pt.safeeat.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InternalServerError {
    private String message;
    private String exception;
}
