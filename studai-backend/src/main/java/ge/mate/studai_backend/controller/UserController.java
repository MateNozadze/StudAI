package ge.mate.studai_backend.controller;


import ge.mate.studai_backend.domain.User;
import ge.mate.studai_backend.dto.response.UserProfileResponse;
import ge.mate.studai_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{userid}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable("userid") Long userId) {
        UserProfileResponse userProfile = userService.getUserProfileDto(userId);
        return ResponseEntity.ok(userProfile);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable("id") Long id,
            @RequestBody User request,
            Authentication authentication
    ) {
        String currentUserEmail = authentication.getName();
        userService.updateUser(id, request, currentUserEmail);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        String currentUserEmail = authentication.getName();
        userService.deleteUser(id, currentUserEmail);
        return ResponseEntity.ok("User deleted successfully");
    }
}
