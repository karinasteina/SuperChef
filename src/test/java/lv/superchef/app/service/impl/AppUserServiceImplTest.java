package lv.superchef.app.service.impl;

import lv.superchef.app.dto.RegisterRequest;
import lv.superchef.app.repository.IAppUserRepo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("App User Service Unit Tests")
public class AppUserServiceImplTest {
    @Mock
    private IAppUserRepo userRepo;

    @InjectMocks
    private AppUserServiceImpl userService;

    @Test
    @DisplayName("Register User: Should throw IllegalArgumentException when required input parameters are null")
    public void testRegisterUserWithNullParamShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.register(new RegisterRequest(null, "testuser1234567", "test@test.lv", "testuser1234567")));

        assertThat(ex.getMessage()).contains("Incorrect input parameters");

    }

    @Test
    @DisplayName("Register User: Should throw IllegalArgumentException when password and confirm password fields do not match")
    public void passwordIsNotTheSameAsConfirmPassword() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.register(new RegisterRequest("testuser", "testuser1234567", "test@test.lv", "testuser12345")));
        assertThat(ex.getMessage()).contains("Passwords do not match");

    }

    @Test
    @DisplayName("Register User: Should throw IllegalArgumentException and block registration when the username is already taken")
    public void registerFailsWithTakenUsername() {
        when(userRepo.existsByUsername("testuser")).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.register(new RegisterRequest("testuser", "testuser1234567", "test@test.lv", "testuser1234567")));
        assertThat(ex.getMessage()).contains("Username is already taken");
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("Register User: Should throw IllegalArgumentException and block registration when the email is already registered")
    public void registerFailsWithTakenEmail() {
        when(userRepo.existsByEmail("test@test.lv")).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.register(new RegisterRequest("testuser", "testuser1234567", "test@test.lv", "testuser1234567")));
        assertThat(ex.getMessage()).contains("Account with this email already exists");
        verify(userRepo, never()).save(any());
    }


}