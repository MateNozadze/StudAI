package ge.mate.studai_backend.service;

import ge.mate.studai_backend.domain.User;

public interface UserService {
    User getUserById(Long id);
    void updateUser(Long id, User request);
    void deleteUser(Long id);
}