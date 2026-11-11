package ge.mate.studai_backend.service.impl;

import ge.mate.studai_backend.domain.User;
import ge.mate.studai_backend.dto.response.UserProfileResponse;
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
    public void updateUser(Long id, User request, String currentUserEmail) {
        User existingUser = getUserById(id);
        if (!existingUser.getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Access Denied: You can only update your own profile!");
        }
        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id,String currentUserEmail) {
        User targetUser = getUserById(id);

        if (!targetUser.getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Access Denied: You can only delete your own profile!");
        }
        userRepository.deleteById(id);
    }


    public UserProfileResponse getUserProfileDto(Long userId){
        User user  = getUserById(userId);

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
