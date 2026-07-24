package lv.superchef.app.controller;

import lv.superchef.app.config.SecurityConfig;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.dto.ReviewFormDTO;
import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.enums.Role;
import lv.superchef.app.model.*;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@ActiveProfiles("test")
@Import({SecurityConfig.class})
@DisplayName("Recipe Controller Integration-Style Web Tests")
class RecipeControllerTest
{
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
    void setUpAuthenticatedUser()
    {
        when(mockUserDetails.getUserId()).thenReturn(USER_ID);
        when(mockUserDetails.getRole()).thenReturn(Role.ROLE_USER);
    }

    private Recipe ownedRecipe()
    {
        AppUser owner = new AppUser();
        ReflectionTestUtils.setField(owner, "id", USER_ID);

        Profile author = new Profile();
        author.setAppUser(owner);
        author.setDisplayName("Mikus");

        Recipe recipe = new Recipe();
        recipe.setAuthor(author);
        recipe.setImageUrl("/images/recipe.jpg");
        return recipe;
    }

    @Nested
    @DisplayName("GET /recipes (List & Search Tests)")
    class ListOfRecipesTests
    {
        @Test
        @DisplayName("GET /recipes (Anonymous): Should return empty favorite set when unauthenticated")
        @WithAnonymousUser
        void listOfRecipes_AnonymousUser_ReturnsEmptyFavorites() throws Exception
        {
            when(recipeService.searchRecipes(null, null, null, null, null, null))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/recipes"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("recipe/list"))
                    .andExpect(model().attribute("loggedIn", false))
                    .andExpect(model().attribute("favoriteRecipeIds", Set.of()));
        }
    }

    @Nested
    @DisplayName("GET /recipes/{id} (Recipe Details Tests)")
    class RecipeDetailsTests
    {

        @Test
        @DisplayName("GET /recipes/{id} (Anonymous): Should load public review stats and metadata cleanly")
        void recipeDetails_AnonymousUser_ShouldRenderDetails() throws Exception
        {
            Long recipeId = 1L;
            Recipe recipe = ownedRecipe();
            ReflectionTestUtils.setField(recipe, "id", recipeId);

            when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);
            when(reviewService.getReviewsByRecipeId(recipeId)).thenReturn(Collections.emptyList());
            when(reviewService.getAverageRating(recipeId)).thenReturn(4.8);
            when(reviewService.getReviewCount(recipeId)).thenReturn(12L);

            mockMvc.perform(get("/recipes/" + recipeId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("recipe/details"))
                    .andExpect(model().attribute("recipe", recipe))
                    .andExpect(model().attribute("averageRating", 4.8))
                    .andExpect(model().attribute("reviewCount", 12L))
                    .andExpect(model().attribute("editingReview", false));
        }

        @Test
        @DisplayName("GET /recipes/{id} (Authenticated): Should load user profile and pre-fill existing review form")
        void recipeDetails_AuthenticatedUser_WithExistingReview_ShouldPopulateReviewForm() throws Exception
        {
            Long recipeId = 1L;
            Recipe recipe = ownedRecipe();
            Profile profile = new Profile();
            ReflectionTestUtils.setField(profile, "id", 10L);

            Review review = new Review();
            review.setRating(5);
            review.setComment("Delicious!");

            when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);
            when(profileService.getProfileByUserId(USER_ID)).thenReturn(Optional.of(profile));
            when(reviewService.getReviewByProfileAndRecipe(10L, recipeId)).thenReturn(Optional.of(review));

            mockMvc.perform(get("/recipes/" + recipeId)
                            .with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("recipe/details"))
                    .andExpect(model().attribute("editingReview", true));
        }

        @Test
        @DisplayName("GET /recipes/{id} (Authenticated): Should throw IllegalStateException when profile missing")
        void recipeDetails_ProfileNotFound_ThrowsException() throws Exception
        {
            Long recipeId = 1L;
            Recipe recipe = ownedRecipe();

            when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);
            when(profileService.getProfileByUserId(USER_ID)).thenReturn(Optional.empty());

            mockMvc.perform(get("/recipes/" + recipeId)
                            .with(user(mockUserDetails)))
                    .andExpect(result -> assertInstanceOf(IllegalStateException.class, result.getResolvedException()));
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
        @DisplayName("GET /recipes/{id}: Should skip default reviewForm creation if model already contains reviewForm")
        void recipeDetails_WithPreexistingReviewFormModel_ShouldNotOverwrite() throws Exception
        {
            Long recipeId = 1L;
            Recipe recipe = ownedRecipe();
            ReflectionTestUtils.setField(recipe, "id", recipeId);

            ReviewFormDTO prefilledForm = new ReviewFormDTO();
            prefilledForm.setComment("Pre-filled comment");

            when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);
            when(reviewService.getReviewsByRecipeId(recipeId)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/recipes/" + recipeId)
                            .flashAttr("reviewForm", prefilledForm))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("reviewForm", prefilledForm));
        }

    }

    @Nested
    @DisplayName("GET /recipes/create & POST /recipes (Creation Tests)")
    class RecipeCreationTests
    {

        @Test
        @DisplayName("GET /recipes/create (Anonymous): Should reject unauthenticated access with 401 Unauthorized")
        @WithAnonymousUser
        void showCreateRecipePage_Anonymous_Returns401() throws Exception
        {
            mockMvc.perform(get("/recipes/create"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /recipes/create (Authenticated): Should build fresh DTO layout structure")
        void showCreateRecipePage_Authenticated_ShouldRenderForm() throws Exception
        {
            when(mockUserDetails.getUsername()).thenReturn("Mikus");

            mockMvc.perform(get("/recipes/create")
                            .with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("recipe/create"))
                    .andExpect(model().attribute("loggedIn", true))
                    .andExpect(model().attribute("authorName", "Mikus"));
        }

        @Test
        @DisplayName("POST /recipes: Should fail validation and return to create view when image is missing")
        void handleCreateRecipe_MissingImage_ReturnsFormWithErrors() throws Exception
        {
            mockMvc.perform(multipart("/recipes")
                            .param("title", "Test")
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("recipe/create"));

            verify(recipeService, never()).createRecipe(any());
        }

        @Test
        @DisplayName("POST /recipes: Should successfully store cover image and redirect")
        void handleCreateRecipe_Success_ShouldSaveAndRedirect() throws Exception
        {
            MockMultipartFile imageFile = new MockMultipartFile("coverImage", "test.jpg", "image/jpeg", "data".getBytes());

            when(imageStorageService.storeCoverImage(any())).thenReturn("/uploads/test.jpg");

            mockMvc.perform(multipart("/recipes")
                            .file(imageFile)
                            .param("title", "Korean Popcorn Chicken")
                            .param("description", "Crispy and savory chicken bites")
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
        @DisplayName("POST /recipes: Should reject when coverImage is provided but empty")
        void handleCreateRecipe_EmptyCoverImage_ReturnsFormWithErrors() throws Exception {
            MockMultipartFile emptyFile = new MockMultipartFile("coverImage", "", "image/jpeg", new byte[0]);

            mockMvc.perform(multipart("/recipes")
                            .file(emptyFile)
                            .param("title", "Test Title")
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("recipe/create"));

            verify(recipeService, never()).createRecipe(any());
        }
    }

    @Nested
    @DisplayName("GET /recipes/{id}/edit & POST /recipes/{id}/update (Edit & Update Tests)")
    class RecipeEditAndUpdateTests
    {
        @Test
        @DisplayName("GET /recipes/{id}/edit: Should 404 if recipe not found")
        void showEditRecipePage_NotFound_Returns404() throws Exception
        {
            when(recipeService.getRecipeById(99L)).thenReturn(null);

            mockMvc.perform(get("/recipes/99/edit")
                            .with(user(mockUserDetails)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /recipes/{id}/edit: Should populate edit form with existing ingredients and steps")
        void showEditRecipePage_Success_PopulatesForm() throws Exception
        {
            Long recipeId = 1L;
            Recipe recipe = ownedRecipe();

            RecipeIngredient ing = new RecipeIngredient();
            ing.setIngredientName("Salmon");
            ing.setQuantity(200.0);
            ing.setUnit(IngredientUnit.G);

            RecipeStep step = new RecipeStep();
            step.setInstruction("Bake salmon");

            recipe.setIngredients(List.of(ing));
            recipe.setSteps(List.of(step));

            when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);

            mockMvc.perform(get("/recipes/" + recipeId + "/edit")
                            .with(user(mockUserDetails)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("recipe/edit"))
                    .andExpect(model().attribute("recipeId", recipeId))
                    .andExpect(model().attribute("isEditMode", true));
        }

        @Test
        @DisplayName("POST /recipes/{id}/update: Should upload and set new cover image when provided")
        void handleUpdateRecipe_WithNewImage_StoresImageAndUpdates() throws Exception
        {
            Long recipeId = 1L;
            Recipe existingRecipe = ownedRecipe();

            MockMultipartFile newImageFile = new MockMultipartFile("coverImage", "new.jpg", "image/jpeg", "new_data".getBytes());

            when(recipeService.getRecipeById(recipeId)).thenReturn(existingRecipe);
            when(imageStorageService.storeCoverImage(any())).thenReturn("/uploads/new-image.jpg");

            mockMvc.perform(multipart("/recipes/" + recipeId + "/update")
                            .file(newImageFile)
                            .param("title", "Updated Title")
                            .param("description", "Updated description")
                            .param("calories", "500")
                            .param("preparationTime", "10")
                            .param("cookingTime", "15")
                            .param("difficulty", "MEDIUM")
                            .param("category", "MAIN_COURSE")
                            .param("ingredients[0].name", "Salmon")
                            .param("ingredients[0].quantity", "200.0")
                            .param("ingredients[0].unit", IngredientUnit.G.name())
                            .param("steps[0]", "Sear in a hot pan")
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/recipes"))
                    .andExpect(flash().attribute("status", "success"));

            verify(imageStorageService, times(1)).storeCoverImage(any());
            verify(recipeService, times(1)).updateRecipe(eq(recipeId), any(RecipeCreateDTO.class));
        }

        @Test
        @DisplayName("POST /recipes/{id}/update: Should keep existing image when coverImage is empty")
        void handleUpdateRecipe_WithoutNewImage_ShouldKeepExistingImage() throws Exception
        {
            Long recipeId = 1L;
            Recipe existingRecipe = ownedRecipe();
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
                            .param("steps[0]", "Sear in a hot pan")
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/recipes"))
                    .andExpect(flash().attribute("status", "success"));

            verify(imageStorageService, never()).storeCoverImage(any());
            verify(recipeService, times(1)).updateRecipe(eq(recipeId), any(RecipeCreateDTO.class));
        }

        @Test
        @DisplayName("GET /recipes/{id}/edit: Should set authorName to 'Unknown' when recipe author is null (Admin edit scenario)")
        void showEditRecipePage_NullAuthor_SetsUnknownAuthorName() throws Exception
        {
            Long recipeId = 1L;
            Recipe recipeWithoutAuthor = new Recipe();
            recipeWithoutAuthor.setIngredients(Collections.emptyList());
            recipeWithoutAuthor.setSteps(Collections.emptyList());

            AppUserDetails adminDetails = mock(AppUserDetails.class);
            when(adminDetails.getUserId()).thenReturn(100L);
            when(adminDetails.getRole()).thenReturn(Role.ROLE_ADMIN);

            when(recipeService.getRecipeById(recipeId)).thenReturn(recipeWithoutAuthor);

            mockMvc.perform(get("/recipes/" + recipeId + "/edit")
                            .with(user(adminDetails)))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("authorName", "Unknown"));
        }
    }

    @Nested
    @DisplayName("POST /recipes/{id}/delete & Ownership Security Branch Tests")
    class RecipeDeleteAndSecurityTests
    {

        @Test
        @DisplayName("POST /recipes/{id}/delete: Should trigger delete pipeline and add flash attributes")
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

        @Test
        @DisplayName("Security check: Should return 401 Unauthorized when unauthenticated user attempts delete operation")
        @WithAnonymousUser
        void requireRecipeOwner_Unauthenticated_Returns403() throws Exception
        {
            Long recipeId = 5L;
            when(recipeService.getRecipeById(recipeId)).thenReturn(ownedRecipe());

            mockMvc.perform(post("/recipes/" + recipeId + "/delete").with(csrf()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Ownership check: Should return 403 Forbidden when regular user tries to delete another user's recipe")
        void requireRecipeOwner_NotOwner_Returns403() throws Exception
        {
            Long recipeId = 5L;
            Recipe notOwnedRecipe = new Recipe();

            AppUser otherUser = new AppUser();
            ReflectionTestUtils.setField(otherUser, "id", 999L);
            Profile otherProfile = new Profile();
            otherProfile.setAppUser(otherUser);
            notOwnedRecipe.setAuthor(otherProfile);

            when(recipeService.getRecipeById(recipeId)).thenReturn(notOwnedRecipe);

            mockMvc.perform(post("/recipes/" + recipeId + "/delete")
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Ownership check: Should allow ADMIN user to bypass ownership check and delete")
        void requireRecipeOwner_AdminUser_AllowsAction() throws Exception
        {
            Long recipeId = 5L;
            Recipe notOwnedRecipe = new Recipe();

            AppUser otherUser = new AppUser();
            ReflectionTestUtils.setField(otherUser, "id", 999L);
            Profile otherProfile = new Profile();
            otherProfile.setAppUser(otherUser);
            notOwnedRecipe.setAuthor(otherProfile);

            AppUserDetails adminDetails = mock(AppUserDetails.class);
            when(adminDetails.getUserId()).thenReturn(100L);
            when(adminDetails.getRole()).thenReturn(Role.ROLE_ADMIN);

            when(recipeService.getRecipeById(recipeId)).thenReturn(notOwnedRecipe);

            mockMvc.perform(post("/recipes/" + recipeId + "/delete")
                            .with(user(adminDetails))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/recipes"));

            verify(recipeService).deleteRecipe(recipeId);
        }

        @Test
        @DisplayName("requireRecipeOwner: Should return 403 Forbidden when recipe author object is null")
        void requireRecipeOwner_NullAuthor_Returns403() throws Exception
        {
            Long recipeId = 5L;
            Recipe recipeWithoutAuthor = new Recipe();

            when(recipeService.getRecipeById(recipeId)).thenReturn(recipeWithoutAuthor);

            mockMvc.perform(post("/recipes/" + recipeId + "/delete")
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("requireRecipeOwner: Should return 403 Forbidden when recipe author's appUser is null")
        void requireRecipeOwner_NullAppUser_Returns403() throws Exception
        {
            Long recipeId = 5L;
            Profile authorWithoutUser = new Profile();
            Recipe recipe = new Recipe();
            recipe.setAuthor(authorWithoutUser);

            when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);

            mockMvc.perform(post("/recipes/" + recipeId + "/delete")
                            .with(user(mockUserDetails))
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Security check: Should return 401 Unauthorized when unauthenticated user attempts delete operation")
        @WithAnonymousUser
        void deleteRecipe_Unauthenticated_Returns401() throws Exception
        {
            Long recipeId = 5L;
            when(recipeService.getRecipeById(recipeId)).thenReturn(ownedRecipe());

            mockMvc.perform(post("/recipes/" + recipeId + "/delete")
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }
}