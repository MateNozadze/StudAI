package ge.mate.studai_backend.controller;

import ge.mate.studai_backend.config.JwtTokenProvider;
import ge.mate.studai_backend.domain.User;
import ge.mate.studai_backend.dto.response.AuthResponse;
import ge.mate.studai_backend.dto.response.JwtResponse;
import ge.mate.studai_backend.dto.request.LoginRequest;
import ge.mate.studai_backend.dto.request.RegisterRequest;
import ge.mate.studai_backend.repository.UserRepository;
import ge.mate.studai_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}