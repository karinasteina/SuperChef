package lv.superchef.app.controller;

import lv.superchef.app.config.SecurityConfig;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IFavoriteRecipeService;
import lv.superchef.app.service.IProfileService;
import lv.superchef.app.service.IRecipeService;
import lv.superchef.app.service.TextService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@DisplayName("Home Controller Unit Tests")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRecipeService recipeService;

    @MockitoBean
    private IFavoriteRecipeService favoriteRecipeService;

    @MockitoBean
    private IProfileService profileService;

    @MockitoBean
    private AppUserDetails mockUserDetails;

    @MockitoBean
    private TextService textService;


    @Nested
    @DisplayName("GET / (Home Page)")
    class HomePageTests {

        @Test
        @DisplayName("Should return home-view with recipes and empty favorites for unauthenticated user")
        void showHomePage_Anonymous_ShouldReturnHomeViewWithEmptyFavorites() throws Exception {
            when(recipeService.searchRecipes(anyString(), anyString(), anyString(), any(), any(), any(), anyInt()))
                    .thenReturn(List.of());
            when(profileService.getAllProfiles()).thenReturn(List.of());

            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("home-view"))
                    .andExpect(model().attribute("activePage", "home"))
                    .andExpect(model().attribute("loggedIn", false))
                    .andExpect(model().attribute("favoriteRecipeIds", Set.of()))
                    .andExpect(model().attributeExists("dinnerRecipes", "tenMinuteRecipes", "hardRecipes", "chefs"));

            verify(favoriteRecipeService, never()).getFavoriteRecipeIdsByUserId(anyLong());
            verify(profileService).getAllProfiles();
        }

    }

    @Nested
    @DisplayName("GET /feed")
    class FeedPageTests {

        @Test
        @DisplayName("Should reject unauthenticated user")
        void feed_Anonymous_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/feed"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(recipeService, favoriteRecipeService);
        }

        @Test
        @DisplayName("Should return feed view with favorite IDs for authenticated user")
        void feed_Authenticated_ShouldReturnFeedViewWithFavorites() throws Exception {
            Long userId = 2L;
            Set<Long> favoriteIds = Set.of(5L);

            when(mockUserDetails.getUserId()).thenReturn(userId);
            when(favoriteRecipeService.getFavoriteRecipeIdsByUserId(userId)).thenReturn(favoriteIds);
            when(recipeService.searchRecipes("", "", null, null, null, null)).thenReturn(List.of());

            mockMvc.perform(get("/feed").with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("feed"))
                    .andExpect(model().attribute("activePage", "feed"))
                    .andExpect(model().attribute("loggedIn", true))
                    .andExpect(model().attribute("favoriteRecipeIds", favoriteIds));

            verify(favoriteRecipeService, times(1)).getFavoriteRecipeIdsByUserId(userId);
        }
    }

}
