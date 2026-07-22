package lv.superchef.app.controller;

import lv.superchef.app.config.SecurityConfig;
import lv.superchef.app.dto.RegisterRequest;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IAppUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@DisplayName("Auth Controller Unit Tests")
class AuthControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAppUserService userService;

    @MockitoBean
    private AppUserDetails mockUserDetails;

    @Nested
    @DisplayName("GET /login")
    class LoginViewTests
    {
        @Test
        @DisplayName("Should return login view")
        void getControllerShowLogInPage_ShouldReturnLoginView() throws Exception {
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login-view"));
        }
    }

    @Nested
    @DisplayName("GET /register")
    class RegisterViewTests
    {
        @Test
        @DisplayName("Should return register form view with empty RegisterRequest model attribute")
        void getControllerRegisterUser_ShouldReturnRegisterViewWithModel() throws Exception
        {
            mockMvc.perform(get("/register"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("register-form-view"))
                    .andExpect(model().attributeExists("registerRequest"));
        }
    }

    @Nested
    @DisplayName("POST /register")
    class RegisterActionTests
    {
        @Test
        @WithMockUser
        @DisplayName("Should register user successfully and redirect to login page")
        void postControllerRegisterUser_Success_ShouldRedirectToLogin() throws Exception
        {
            mockMvc.perform(post("/register")
                            .with(user(mockUserDetails))
                            .with(csrf())
                            .param("username", "john_doe")
                            .param("email", "john@example.com")
                            .param("password", "Password123!")
                            .param("confirmPassword", "Password123!"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login?registered"));

            verify(userService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return register view when validation errors occur")
        void postControllerRegisterUser_ValidationError_ShouldReturnRegisterView() throws Exception
        {
            mockMvc.perform(post("/register")
                            .with(csrf())
                            .with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("register-form-view"))
                    .andExpect(model().hasErrors());

            verify(userService, never()).register(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return register view with error message when userService throws IllegalArgumentException")
        void postControllerRegisterUser_ServiceThrowsException_ShouldReturnRegisterViewWithError() throws Exception
        {
            String errorMessage = "Username or email is already taken";
            doThrow(new IllegalArgumentException(errorMessage))
                    .when(userService).register(any(RegisterRequest.class));

            mockMvc.perform(post("/register")
                            .with(user(mockUserDetails))
                            .with(csrf())
                            .param("username", "existing_user")
                            .param("email", "existing@example.com")
                            .param("password", "Password123!")
                            .param("confirmPassword", "Password123!"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("register-form-view"))
                    .andExpect(model().attribute("error", errorMessage));

            verify(userService, times(1)).register(any(RegisterRequest.class));
        }
    }
}