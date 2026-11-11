package ge.mate.studai_backend.service;

import ge.mate.studai_backend.domain.User;
import ge.mate.studai_backend.dto.response.UserProfileResponse;

public interface UserService {
    User getUserById(Long id);
    void updateUser(Long id, User request, String currentUserEmail);
    void deleteUser(Long id, String currentUserEmail);
    UserProfileResponse getUserProfileDto(Long userId);
}