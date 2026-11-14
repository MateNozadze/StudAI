package ge.mate.studai_backend.service.impl;

import ge.mate.studai_backend.config.JwtTokenProvider;
import ge.mate.studai_backend.domain.User;
import ge.mate.studai_backend.dto.request.LoginRequest;
import ge.mate.studai_backend.dto.request.RegisterRequest;
import ge.mate.studai_backend.dto.response.AuthResponse;
import ge.mate.studai_backend.repository.UserRepository;
import ge.mate.studai_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        String token = tokenProvider.generateToken(user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(user.getEmail());
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return response;
    }
}