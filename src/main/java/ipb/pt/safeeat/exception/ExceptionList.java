package ipb.pt.safeeat.exception;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionList {
    private String message;
    private List<String> errors;
}
