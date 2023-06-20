package ipb.pt.safeeat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class  Home {
    private List<Content> content;
}
