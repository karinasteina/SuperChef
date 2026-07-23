package lv.superchef.app.controller;

import lv.superchef.app.config.SecurityConfig;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RecipeController.class)
@ActiveProfiles("test")
@Import({SecurityConfig.class})
@DisplayName("Recipe Controller Integration-Style Web Tests")
class RecipeControllerTest {

    private static final Long USER_ID = 42L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRecipeService recipeService;

    @MockitoBean
    private IFavoriteRecipeService favoriteRecipeService;

    @MockitoBean
    private IImageStorageService imageStorageService;

    @MockitoBean
    private IReviewService reviewService;

    @MockitoBean
    private IProfileService profileService;

    @MockitoBean
    private lv.superchef.app.repository.IAppUserRepo appUserRepo;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @MockitoBean
    private AppUserDetails mockUserDetails;

    @BeforeEach
    void setUpAuthenticatedUser() {
        when(mockUserDetails.getUserId()).thenReturn(USER_ID);
    }

    private Recipe ownedRecipe() {
        AppUser owner = new AppUser();
        ReflectionTestUtils.setField(owner, "id", USER_ID);

        Profile author = new Profile();
        author.setAppUser(owner);

        Recipe recipe = new Recipe();
        recipe.setAuthor(author);
        return recipe;
    }



    @Test
    @DisplayName("GET /recipes/{id} (Anonymous): Should load all public review stats and metadata cleanly")
    void recipeDetails_AnonymousUser_ShouldRenderDetails() throws Exception
    {
        Long recipeId = 1L;
        Recipe recipe = new Recipe();
        ReflectionTestUtils.setField(recipe, "id", recipeId);
        recipe.setImageUrl("/images/shashlik.jpg");

        when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);
        when(reviewService.getReviewsByRecipeId(recipeId)).thenReturn(Collections.emptyList());
        when(reviewService.getAverageRating(recipeId)).thenReturn(4.8);
        when(reviewService.getReviewCount(recipeId)).thenReturn(12L);

        mockMvc.perform(get("/recipes/" + recipeId)
                        .requestAttr("javax.servlet.include.request_uri", "http://localhost/recipes/" + recipeId))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/details"))
                .andExpect(model().attribute("recipe", recipe))
                .andExpect(model().attribute("averageRating", 4.8))
                .andExpect(model().attribute("reviewCount", 12L))
                .andExpect(model().attribute("editingReview", false));
    }

    @Test
    @DisplayName("GET /recipes/{id}: Should immediately respond with 404 Not Found when key lookup fails")
    @WithAnonymousUser
    void recipeDetails_RecipeNotFound_ShouldThrow404() throws Exception
    {
        when(recipeService.getRecipeById(99L)).thenReturn(null);

        mockMvc.perform(get("/recipes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /recipes/create: Should build fresh DTO layout structure with supported structural units")
    void showCreateRecipePage_ShouldRenderCreationForm() throws Exception
    {
        mockMvc.perform(get("/recipes/create")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/create"))
                .andExpect(model().attributeExists("createRecipeDto"))
                .andExpect(model().attributeExists("ingredientUnits"));
    }

    @Test
    @DisplayName("POST /recipes: Should successfully store uploaded cover image file and redirect to overview index")
    void handleCreateRecipe_ShouldSaveAndRedirect() throws Exception
    {
        MockMultipartFile imageFile = new MockMultipartFile("coverImage", "test.jpg", "image/jpeg", "image-data".getBytes());

        when(imageStorageService.storeCoverImage(any())).thenReturn("/uploads/test.jpg");

        mockMvc.perform(multipart("/recipes")
                        .file(imageFile)
                        .param("title", "Korean Popcorn Chicken")
                        .param("description", "Crispy, baked, savory delicious chicken bites")
                        .param("calories", "450")
                        .param("preparationTime", "15")
                        .param("cookingTime", "20")
                        .param("difficulty", "EASY")
                        .param("category", "MAIN_COURSE")
                        .param("ingredients[0].name", "Chicken")
                        .param("ingredients[0].quantity", "500.0")
                        .param("ingredients[0].unit", IngredientUnit.G.name())
                        .param("steps[0]", "Bake at 200°C until crispy")
                        .with(user(mockUserDetails))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipes?success=true"));

        verify(recipeService, times(1)).createRecipe(any(RecipeCreateDTO.class));
    }

    @Test
    @DisplayName("GET /recipes/{id}/edit: Should successfully map existing item structure to conversion edit target DTO")
    void showEditRecipePage_ShouldPopulateForm() throws Exception
    {
        Long recipeId = 1L;
        Recipe recipe = ownedRecipe();
        recipe.setTitle("Low Poly Baked Salmon");
        recipe.setIngredients(Collections.emptyList());
        recipe.setSteps(Collections.emptyList());

        when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);

        mockMvc.perform(get("/recipes/" + recipeId + "/edit")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/edit"))
                .andExpect(model().attribute("recipeId", recipeId))
                .andExpect(model().attribute("isEditMode", true));
    }

    @Test
    @DisplayName("POST /recipes/{id}/update: Should maintain previous path state image link if new upload argument is absent")
    void handleUpdateRecipe_WithoutNewImage_ShouldKeepExistingImage() throws Exception
    {
        Long recipeId = 1L;
        Recipe existingRecipe = ownedRecipe();
        ReflectionTestUtils.setField(existingRecipe, "id", recipeId);
        existingRecipe.setImageUrl("/uploads/old-image.jpg");

        when(recipeService.getRecipeById(recipeId)).thenReturn(existingRecipe);

        mockMvc.perform(post("/recipes/" + recipeId + "/update")
                        .param("title", "Updated Title")
                        .param("description", "Updated description for testing")
                        .param("calories", "500")
                        .param("preparationTime", "10")
                        .param("cookingTime", "15")
                        .param("difficulty", "MEDIUM")
                        .param("category", "MAIN_COURSE")
                        .param("ingredients[0].name", "Salmon")
                        .param("ingredients[0].quantity", "200.0")
                        .param("ingredients[0].unit", IngredientUnit.G.name())
                        .param("steps[0]", "Sear in a hot pot")
                        .with(user(mockUserDetails))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipes"))
                .andExpect(flash().attribute("status", "success"));

        verify(imageStorageService, never()).storeCoverImage(any());
        verify(recipeService, times(1)).updateRecipe(eq(recipeId), any(RecipeCreateDTO.class));
    }

    @Test
    @DisplayName("🗑POST /recipes/{id}/delete: Should trigger delete operation pipeline and append success status flashes")
    void handleDeleteRecipe_ShouldExecuteAndRedirect() throws Exception
    {
        Long recipeId = 5L;
        when(recipeService.getRecipeById(recipeId)).thenReturn(ownedRecipe());

        mockMvc.perform(post("/recipes/" + recipeId + "/delete")
                        .with(user(mockUserDetails))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipes"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("status", "success"));

        verify(recipeService, times(1)).deleteRecipe(recipeId);
    }
}
