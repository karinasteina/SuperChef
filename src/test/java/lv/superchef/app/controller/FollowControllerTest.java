package lv.superchef.app.controller;

import lv.superchef.app.config.SecurityConfig;
import lv.superchef.app.model.Profile;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IFollowService;
import lv.superchef.app.service.IProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FollowController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@DisplayName("Follow Controller Unit Tests")
class FollowControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IFollowService followService;

    @MockitoBean
    private IProfileService profileService;

    @MockitoBean
    private AppUserDetails mockUserDetails;

    private Profile followerProfile;

    @BeforeEach
    void setUp()
    {
        followerProfile = new Profile();
        ReflectionTestUtils.setField(followerProfile, "id", 10L);
    }

    @Nested
    @DisplayName("POST /profiles/follow/{followingProfileId}")
    class FollowTests
    {

        @Test
        @DisplayName("Should return 204 No Content when follow operation succeeds without returnTo")
        void follow_Success_ShouldReturn204() throws Exception
        {
            Long userId = 1L;
            Long targetProfileId = 20L;

            when(mockUserDetails.getUserId()).thenReturn(userId);
            when(profileService.getProfileByUserId(userId)).thenReturn(Optional.of(followerProfile));

            mockMvc.perform(post("/profiles/follow/{followingProfileId}", targetProfileId)
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(followService, times(1)).follow(10L, targetProfileId);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when user profile is not found")
        void follow_ProfileNotFound_ShouldReturn401() throws Exception
        {
            Long userId = 1L;
            Long targetProfileId = 20L;

            when(mockUserDetails.getUserId()).thenReturn(userId);
            when(profileService.getProfileByUserId(userId)).thenReturn(Optional.empty());

            mockMvc.perform(post("/profiles/follow/{followingProfileId}", targetProfileId)
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verify(followService, never()).follow(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Should trigger controller's explicit userDetails == null guard when custom principal is missing")
        @WithMockUser // Passes security filter, but injects null for @AuthenticationPrincipal AppUserDetails
        void follow_NullAppUserDetails_TriggersExplicitNullGuard() throws Exception
        {
            mockMvc.perform(post("/profiles/follow/{followingProfileId}", 20L)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verify(followService, never()).follow(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("POST /profiles/unfollow/{unfollowProfileId}")
    class UnfollowTests
    {

        @Test
        @DisplayName("Should return 204 No Content when unfollow operation succeeds without returnTo")
        void unfollow_Success_ShouldReturn204() throws Exception
        {
            Long userId = 1L;
            Long targetProfileId = 20L;

            when(mockUserDetails.getUserId()).thenReturn(userId);
            when(profileService.getProfileByUserId(userId)).thenReturn(Optional.of(followerProfile));

            mockMvc.perform(post("/profiles/unfollow/{unfollowProfileId}", targetProfileId)
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(followService, times(1)).unfollow(10L, targetProfileId);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when user profile is not found")
        void unfollow_ProfileNotFound_ShouldReturn401() throws Exception
        {
            Long userId = 1L;
            Long targetProfileId = 20L;

            when(mockUserDetails.getUserId()).thenReturn(userId);
            when(profileService.getProfileByUserId(userId)).thenReturn(Optional.empty());

            mockMvc.perform(post("/profiles/unfollow/{unfollowProfileId}", targetProfileId)
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verify(followService, never()).unfollow(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Should trigger controller's explicit userDetails == null guard when custom principal is missing")
        @WithMockUser
        void unfollow_NullAppUserDetails_TriggersExplicitNullGuard() throws Exception
        {
            mockMvc.perform(post("/profiles/unfollow/{unfollowProfileId}", 20L)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verify(followService, never()).unfollow(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("responseAfterChange Redirect Branch Tests")
    class RedirectBranchTests
    {

        @ParameterizedTest
        @CsvSource({
                "profiles, /profiles",
                "profile, /profile",
                "other, /profile/20"
        })
        @DisplayName("Should redirect to correct Location header after follow based on returnTo parameter")
        void follow_WithReturnTo_ShouldRedirectCorrectly(String returnTo, String expectedLocation) throws Exception
        {
            when(mockUserDetails.getUserId()).thenReturn(1L);
            when(profileService.getProfileByUserId(1L)).thenReturn(Optional.of(followerProfile));

            mockMvc.perform(post("/profiles/follow/{followingProfileId}", 20L)
                            .param("returnTo", returnTo)
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isSeeOther())
                    .andExpect(header().string("Location", expectedLocation));

            verify(followService).follow(10L, 20L);
        }

        @ParameterizedTest
        @CsvSource({
                "profiles, /profiles",
                "profile, /profile",
                "other, /profile/20"
        })
        @DisplayName("Should redirect to correct Location header after unfollow based on returnTo parameter")
        void unfollow_WithReturnTo_ShouldRedirectCorrectly(String returnTo, String expectedLocation) throws Exception
        {
            when(mockUserDetails.getUserId()).thenReturn(1L);
            when(profileService.getProfileByUserId(1L)).thenReturn(Optional.of(followerProfile));

            mockMvc.perform(post("/profiles/unfollow/{unfollowProfileId}", 20L)
                            .param("returnTo", returnTo)
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isSeeOther())
                    .andExpect(header().string("Location", expectedLocation));

            verify(followService).unfollow(10L, 20L);
        }
    }
}