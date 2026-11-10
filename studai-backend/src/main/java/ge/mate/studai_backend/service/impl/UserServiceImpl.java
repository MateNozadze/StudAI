package ge.mate.studai_backend.service.impl;

import ge.mate.studai_backend.domain.User;
import ge.mate.studai_backend.repository.UserRepository;
import ge.mate.studai_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public void updateUser(Long id, User request) {
        User existingUser = getUserById(id);

        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
