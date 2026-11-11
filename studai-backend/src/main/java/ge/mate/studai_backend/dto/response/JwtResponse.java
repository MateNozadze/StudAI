package ge.mate.studai_backend.dto.response;

import lombok.Getter;

@Getter
public class JwtResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public JwtResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}