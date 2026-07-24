package lv.superchef.app.controller;

import lv.superchef.app.config.SecurityConfig;
import lv.superchef.app.enums.Role;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.model.FavoriteRecipe;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IFavoriteRecipeService;
import lv.superchef.app.service.IProfileService;
import lv.superchef.app.service.IRecipeService;
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
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoriteRecipesController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@DisplayName("Favorite Recipes Controller Tests")
public class FavoriteRecipesControllerTest
{

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    IFavoriteRecipeService favoriteRecipeService;

    @MockitoBean
    IRecipeService recipeService;

    @MockitoBean
    IProfileService profileService;

    private AppUserDetails authenticatedUser(Long userId) {
        AppUser appUser = mock(AppUser.class);

        when(appUser.getId()).thenReturn(userId);
        when(appUser.getUsername()).thenReturn("Mikus");
        when(appUser.getPassword()).thenReturn("password");
        when(appUser.getRole()).thenReturn(Role.ROLE_USER);

        return new AppUserDetails(appUser);
    }

    @Nested
    @DisplayName("listFavoriteRecipes Branch Tests")
    class ListFavoriteRecipesTests
    {

        @Test
        @DisplayName("Should return recipes and favorite IDs for authenticated user")
        void listFavorites_authenticatedUser_returnsRecipesAndFavoriteIds() throws Exception
        {
            AppUserDetails userDetails = authenticatedUser(42L);
            FavoriteRecipe favoriteRecipe = mock(FavoriteRecipe.class);
            Recipe recipe = mock(Recipe.class);

            when(recipe.getId()).thenReturn(67L);
            when(favoriteRecipe.getRecipe()).thenReturn(recipe);
            when(favoriteRecipeService.getAllFavoriteRecipesByUserId(42L))
                    .thenReturn(List.of(favoriteRecipe));

            mockMvc.perform(get("/recipes/favorites")
                            .with(user(userDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("favorite-list"))
                    .andExpect(model().attribute("recipes", List.of(recipe)))
                    .andExpect(model().attribute("favoriteRecipeIds", Set.of(67L)))
                    .andExpect(model().attribute("loggedIn", true));
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when user is not authenticated")
        void listFavorites_unauthenticatedUser_redirectsToLogin() throws Exception
        {
            mockMvc.perform(get("/recipes/favorites"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("addFavoriteRecipe Branch Tests")
    class AddFavoriteRecipeTests
    {

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void addFavorite_unauthenticatedUser_returns401() throws Exception
        {
            mockMvc.perform(post("/recipes/favorites/67").with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(recipeService, profileService, favoriteRecipeService);
        }

        @Test
        @DisplayName("Should return 404 when recipe does not exist")
        void addFavorite_recipeNotFound_returns404() throws Exception
        {
            AppUserDetails userDetails = authenticatedUser(42L);

            when(recipeService.getRecipeById(67L)).thenReturn(null);

            mockMvc.perform(post("/recipes/favorites/67")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().isNotFound());

            verify(recipeService).getRecipeById(67L);
            verifyNoInteractions(profileService, favoriteRecipeService);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when profile does not exist")
        void addFavorite_profileNotFound_throwsException() throws Exception
        {
            AppUserDetails userDetails = authenticatedUser(42L);
            Recipe recipe = mock(Recipe.class);

            when(recipeService.getRecipeById(67L)).thenReturn(recipe);
            when(profileService.getProfileByUserId(42L)).thenReturn(Optional.empty());

            mockMvc.perform(post("/recipes/favorites/67")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(result ->
                            assertInstanceOf(IllegalStateException.class, result.getResolvedException())
                    );
        }

        @Test
        @DisplayName("Should add to favorites and return 204 No Content")
        void addFavorite_success_returns204() throws Exception
        {
            AppUserDetails userDetails = authenticatedUser(42L);
            Recipe recipe = mock(Recipe.class);
            Profile profile = mock(Profile.class);

            when(recipeService.getRecipeById(67L)).thenReturn(recipe);
            when(profileService.getProfileByUserId(42L)).thenReturn(Optional.of(profile));

            mockMvc.perform(post("/recipes/favorites/67")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(favoriteRecipeService).addToFavorites(recipe, profile);
        }
    }

    @Nested
    @DisplayName("removeFavoriteRecipe Branch Tests")
    class RemoveFavoriteRecipeTests
    {

        @Test
        @DisplayName("Should return 401 when user is not authenticated")
        void removeFavorite_unauthenticatedUser_returns401() throws Exception
        {
            mockMvc.perform(post("/recipes/favorites/67/unfavorite").with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(recipeService, profileService, favoriteRecipeService);
        }

        @Test
        @DisplayName("Should return 404 when recipe does not exist")
        void removeFavorite_recipeNotFound_returns404() throws Exception
        {
            AppUserDetails userDetails = authenticatedUser(42L);

            when(recipeService.getRecipeById(67L)).thenReturn(null);

            mockMvc.perform(post("/recipes/favorites/67/unfavorite")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().isNotFound());

            verify(recipeService).getRecipeById(67L);
            verifyNoInteractions(profileService, favoriteRecipeService);
        }

        @Test
        @DisplayName("Should remove from favorites and return 204 No Content")
        void removeFavorite_success_returns204() throws Exception
        {
            AppUserDetails userDetails = authenticatedUser(42L);
            Recipe recipe = mock(Recipe.class);
            Profile profile = mock(Profile.class);

            when(recipeService.getRecipeById(67L)).thenReturn(recipe);
            when(profileService.getProfileByUserId(42L)).thenReturn(Optional.of(profile));

            mockMvc.perform(post("/recipes/favorites/67/unfavorite")
                            .with(user(userDetails))
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(favoriteRecipeService).removeFromFavorites(recipe, profile);
        }
    }
}