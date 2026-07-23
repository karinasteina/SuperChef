package lv.superchef.app.controller;

import lv.superchef.app.config.SecurityConfig;
import lv.superchef.app.enums.Role;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.model.FavoriteRecipe;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IFavoriteRecipeService;
import lv.superchef.app.service.IProfileService;
import lv.superchef.app.service.IRecipeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoriteRecipesController.class)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@DisplayName("Favorite Recipes Controller Tests")
public class FavoriteRecipesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IFavoriteRecipeService favoriteRecipeService;

    @MockitoBean
    private IRecipeService recipeService;

    @MockitoBean
    private IProfileService profileService;

    private AppUserDetails authenticatedUser(Long userId) {
        AppUser appUser = mock(AppUser.class);

        when(appUser.getId()).thenReturn(userId);
        when(appUser.getUsername()).thenReturn("Mikus");
        when(appUser.getPassword()).thenReturn("password");
        when(appUser.getRole()).thenReturn(Role.ROLE_USER);

        return new AppUserDetails(appUser);
    }

    @Test
    void listFavorites_authenticatedUser_returnsRecipesAndFavoriteIds() throws Exception {
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
}
