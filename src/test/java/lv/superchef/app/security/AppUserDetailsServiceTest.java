package lv.superchef.app.security;

import lv.superchef.app.enums.Role;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.repository.IAppUserRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppUserDetailsService Unit Tests")
class AppUserDetailsServiceTest
{

    @Mock
    private IAppUserRepo userRepo;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @Test
    @DisplayName("Should return UserDetails when user exists")
    void shouldReturnUserDetailsWhenUserExists()
    {
        String username = "chef_mario";
        AppUser mockUser = new AppUser(username, "encodedPassword", "mario@superchef.lv", Role.ROLE_USER);

        when(userRepo.existsByUsername(username)).thenReturn(true);
        when(userRepo.findByUsername(username)).thenReturn(mockUser);

        UserDetails userDetails = appUserDetailsService.loadUserByUsername(username);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");

        verify(userRepo, times(1)).existsByUsername(username);
        verify(userRepo, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist()
    {
        String username = "unknown_user";

        when(userRepo.existsByUsername(username)).thenReturn(false);

        assertThatThrownBy(() -> appUserDetailsService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Incorrect credentials");

        verify(userRepo, times(1)).existsByUsername(username);
        verify(userRepo, never()).findByUsername(anyString());
    }
}