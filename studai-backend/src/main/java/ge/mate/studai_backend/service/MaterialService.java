package ge.mate.studai_backend.service;

import ge.mate.studai_backend.dto.request.MaterialCreateRequest;
import ge.mate.studai_backend.dto.response.MaterialResponse;

import java.util.List;

public interface MaterialService {
    MaterialResponse createMaterial(MaterialCreateRequest request, Long userId);
    MaterialResponse getMaterialById(Long id);
    List<MaterialResponse> getAllMaterialsForUser(Long userId);
    void deleteMaterial(Long id);
}
