package ge.mate.studai_backend.service.impl;

import ge.mate.studai_backend.domain.Material;
import ge.mate.studai_backend.domain.User;
import ge.mate.studai_backend.dto.request.MaterialCreateRequest;
import ge.mate.studai_backend.dto.response.MaterialResponse;
import ge.mate.studai_backend.repository.MaterialRepository;
import ge.mate.studai_backend.repository.UserRepository;
import ge.mate.studai_backend.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialServiceImpl implements MaterialService {

    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;


    @Override
    public MaterialResponse createMaterial(MaterialCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Material material = new Material();
        material.setTitle(request.getTitle());
        material.setContent(request.getContent());
        material.setUser(user);

        Material savedMaterial = materialRepository.save(material);

        return mapToResponse(savedMaterial);
    }

    @Override
    @Transactional(readOnly = true)
    public MaterialResponse getMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + id));
        return mapToResponse(material);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialResponse> getAllMaterialsForUser(Long userId) {
        List<Material> materials = materialRepository.findByUserId(userId);

        return materials.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMaterial(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Material not found with id: " + id);
        }
        materialRepository.deleteById(id);
    }

    private MaterialResponse mapToResponse(Material material) {
        MaterialResponse response = new MaterialResponse();
        response.setId(material.getId());
        response.setTitle(material.getTitle());
        response.setContent(material.getContent());
        return response;
    }
}
