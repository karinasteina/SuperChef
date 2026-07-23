package lv.superchef.app.controller;

import jakarta.servlet.ServletException;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IProfileService;
import lv.superchef.app.service.IRecipeService;
import lv.superchef.app.service.IReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewController Standalone Unit Tests")
class ReviewControllerTest
{

    private static final Long USER_ID = 1L;
    private static final Long PROFILE_ID = 10L;
    private static final Long RECIPE_ID = 100L;

    private MockMvc mockMvc;

    @Mock
    private IReviewService reviewService;

    @Mock
    private IRecipeService recipeService;

    @Mock
    private IProfileService profileService;

    @Mock
    private AppUserDetails mockUserDetails;

    @InjectMocks
    private ReviewController reviewController;

    private UsernamePasswordAuthenticationToken authToken;
    private Profile mockProfile;
    private Recipe mockRecipe;

    @BeforeEach
    void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        lenient().when(mockUserDetails.getUserId()).thenReturn(USER_ID);

        authToken = new UsernamePasswordAuthenticationToken(
                mockUserDetails,
                null,
                mockUserDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        mockProfile = createMockProfile(USER_ID, PROFILE_ID);
        mockRecipe = createMockRecipe(RECIPE_ID);
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    private Profile createMockProfile(Long userId, Long profileId)
    {
        AppUser user = new AppUser();
        ReflectionTestUtils.setField(user, "id", userId);

        Profile profile = new Profile();
        ReflectionTestUtils.setField(profile, "id", profileId);
        profile.setAppUser(user);
        return profile;
    }

    private Recipe createMockRecipe(Long recipeId)
    {
        Recipe recipe = new Recipe();
        ReflectionTestUtils.setField(recipe, "id", recipeId);
        return recipe;
    }

    @Nested
    @DisplayName("POST /recipes/{recipeId}/reviews Tests")
    class SaveReviewTests
    {

        @Test
        @DisplayName("Should redirect to /login when user is unauthenticated")
        void shouldRedirectToLoginWhenUnauthenticated() throws Exception
        {
            SecurityContextHolder.clearContext();

            mockMvc.perform(post("/recipes/100/reviews")
                            .param("rating", "5")
                            .param("comment", "Great recipe!"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));

            verifyNoInteractions(profileService, recipeService, reviewService);
        }

        @Test
        @DisplayName("Should redirect back to recipe view with flash attributes when validation fails")
        void shouldRedirectWithFlashAttributesWhenValidationFails() throws Exception
        {
            mockMvc.perform(post("/recipes/100/reviews")
                            .principal(authToken))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/recipes/100"))
                    .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.reviewForm"))
                    .andExpect(flash().attributeExists("reviewForm"));

            verifyNoInteractions(profileService, recipeService, reviewService);
        }

        @Test
        @DisplayName("Should throw ResponseStatusException 404 when recipe is not found")
        void shouldThrowNotFoundWhenRecipeDoesNotExist() throws Exception
        {
            when(profileService.getProfileByUserId(USER_ID)).thenReturn(Optional.of(mockProfile));
            when(recipeService.getRecipeById(999L)).thenReturn(null);

            mockMvc.perform(post("/recipes/999/reviews")
                            .principal(authToken)
                            .param("rating", "5")
                            .param("comment", "Delicious!"))
                    .andExpect(status().isNotFound());

            verify(reviewService, never()).upsertReview(any());
        }

        @Test
        @DisplayName("Should throw IllegalStateException when user profile is not found")
        void shouldThrowExceptionWhenProfileNotFound()
        {
            when(profileService.getProfileByUserId(USER_ID)).thenReturn(Optional.empty());

          assertThrows(ServletException.class, () -> mockMvc.perform(post("/recipes/100/reviews")
                            .principal(authToken)
                            .param("rating", "5")
                            .param("comment", "Delicious!"))
            );

            verifyNoInteractions(recipeService, reviewService);
        }

        @Test
        @DisplayName("Should save review and redirect with success message when input is valid")
        void shouldSaveReviewAndRedirectWhenValid() throws Exception
        {
            when(profileService.getProfileByUserId(USER_ID)).thenReturn(Optional.of(mockProfile));
            when(recipeService.getRecipeById(RECIPE_ID)).thenReturn(mockRecipe);

            mockMvc.perform(post("/recipes/100/reviews")
                            .principal(authToken)
                            .param("rating", "5")
                            .param("comment", "  Awesome dish!  "))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/recipes/100"))
                    .andExpect(flash().attribute("reviewSuccess", "Your review was saved successfully."));

            verify(reviewService, times(1)).upsertReview(argThat(review ->
                    review.getProfile().equals(mockProfile) &&
                            review.getRecipe().equals(mockRecipe) &&
                            review.getRating() == 5 &&
                            "Awesome dish!".equals(review.getComment())
            ));
        }
    }
}