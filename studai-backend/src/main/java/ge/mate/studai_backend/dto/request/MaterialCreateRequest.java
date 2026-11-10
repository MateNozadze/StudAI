package ge.mate.studai_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCreateRequest {
    private String title;
    private String content;
    private Long userId;
}
