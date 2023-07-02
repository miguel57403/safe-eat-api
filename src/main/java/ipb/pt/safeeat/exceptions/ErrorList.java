package ipb.pt.safeeat.exceptions;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorList {
    private String message;
    private List<String> errors;
}
