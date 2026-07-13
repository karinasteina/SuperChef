package lv.superchef.app.service.impl;

import lv.superchef.app.dto.RegisterRequest;
import lv.superchef.app.repository.IAppUserRepo;

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
public class AppUserServiceImplTest {
    @Mock
    private IAppUserRepo userRepo;

    @InjectMocks
    private AppUserServiceImpl userService;

    @Test
    public void testRegisterUserWithNullParamShouldThrow(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.register(new RegisterRequest(null, "testuser1234567", "test@test.lv", "testuser1234567")));

        assertThat(ex.getMessage()).contains("Incorrect input parameters");

    }

    @Test
    public void passwordIsNotTheSameAsConfirmPassword(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.register(new RegisterRequest("testuser", "testuser1234567", "test@test.lv", "testuser12345")));
        assertThat(ex.getMessage()).contains("Passwords do not match");

    }

    @Test
    public void registerFailsWithTakenUsername(){
        when(userRepo.existsByUsername("testuser")).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.register(new RegisterRequest("testuser", "testuser1234567", "test@test.lv", "testuser1234567")));
        assertThat(ex.getMessage()).contains("Username is already taken");
        verify(userRepo, never()).save(any());
    }

    @Test
    public void registerFailsWithTakenEmail(){
        when(userRepo.existsByEmail("test@test.lv")).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.register(new RegisterRequest("testuser", "testuser1234567", "test@test.lv", "testuser1234567")));
        assertThat(ex.getMessage()).contains("Account with this email already exists");
        verify(userRepo, never()).save(any());
    }


}
