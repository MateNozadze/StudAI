package ge.mate.studai_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
