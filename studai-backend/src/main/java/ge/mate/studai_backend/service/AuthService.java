package ge.mate.studai_backend.service;

import ge.mate.studai_backend.dto.request.LoginRequest;
import ge.mate.studai_backend.dto.request.RegisterRequest;
import ge.mate.studai_backend.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
