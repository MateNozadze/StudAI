package ge.mate.studai_backend.controller;

import ge.mate.studai_backend.domain.User;
import ge.mate.studai_backend.dto.request.MaterialCreateRequest;
import ge.mate.studai_backend.dto.response.MaterialResponse;
import ge.mate.studai_backend.repository.UserRepository;
import ge.mate.studai_backend.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<MaterialResponse> createMaterial(
            @RequestBody MaterialCreateRequest request,
            Principal principal
    ) {
        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        MaterialResponse response = materialService.createMaterial(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> getAllMaterialsForCurrentUser(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        List<MaterialResponse> materials = materialService.getAllMaterialsForUser(user.getId());
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable Long id) {
        MaterialResponse response = materialService.getMaterialById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}