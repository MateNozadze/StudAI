package ge.mate.studai_backend.service.impl;

import ge.mate.studai_backend.domain.User;
import ge.mate.studai_backend.dto.response.UserProfileResponse;
import ge.mate.studai_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void shouldReturnUserByID(){
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userServiceImpl.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUserNotFoundById(){
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userServiceImpl.getUserById(userId)
        );

        assertEquals("User not found with id: " + userId, exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void shouldTestUpdateUser(){
        Long userId = 1L;
        String email = "test@gmail.com";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail(email);

        User updateRequest = new User();
        updateRequest.setEmail("new@example.com");
        updateRequest.setUsername("newUsername");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userServiceImpl.updateUser(userId, updateRequest,email);


        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("newUsername", existingUser.getUsername());
        verify(userRepository , times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }


    @Test
    void shouldThrowAccessDeniedWhenEmailDoesNotMatch(){
        Long userId = 1L;


        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("owner@gmail.com");
        existingUser.setUsername("owner");

        User updateRequest = new User();
        updateRequest.setEmail("hacker@example.com");
        updateRequest.setUsername("hacker");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        RuntimeException exception=assertThrows(
                RuntimeException.class,
                () ->userServiceImpl.updateUser(userId,updateRequest,"other@example.com")
        );

        assertEquals("Access Denied: You can only update your own profile!", exception.getMessage());
        verify(userRepository,times(1)).findById(userId);
        verify(userRepository, never()).save(any());


    }

    @Test
    void shouldCheckDeleteUser() {
        long userId = 1L;
        String userEmail = "other@example.com";

        User user = new User();
        user.setEmail(userEmail);
        user.setId( userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userServiceImpl.deleteUser(userId,userEmail);

        verify(userRepository,times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void shouldThrowAccessDeniedWhenDeletingAnotherUser(){
        long userId = 1L;


        User user = new User();
        user.setEmail("owner@example.com");
        user.setId( userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userServiceImpl.deleteUser(userId,"other@example.com")
        );

        assertEquals("Access Denied: You can only delete your own profile!", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void shouldThrowWhenUserToDeleteNotFound(){
        Long userId =1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userServiceImpl.deleteUser(userId,"other@example.com"));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(any());
    }


    @Test
    void shouldFindUserByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setUsername("test");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserProfileResponse result = userServiceImpl.getProfileByEmail(email);

        assertNotNull(result);
        assertEquals("test", result.getUsername());
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldReturnUserProfileDtoById(){
        long userId = 1L;
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("test");
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserProfileResponse result = userServiceImpl.getUserProfileDto(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

}