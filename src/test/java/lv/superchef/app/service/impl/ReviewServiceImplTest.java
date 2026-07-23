package lv.superchef.app.service.impl;

import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.model.Review;
import lv.superchef.app.repository.IReviewRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewServiceImpl Unit Tests")
class ReviewServiceImplTest
{

    @Mock
    private IReviewRepo reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Nested
    @DisplayName("getReviewByProfileAndRecipe Tests")
    class GetReviewByProfileAndRecipeTests
    {

        @Test
        @DisplayName("Should return Optional containing Review when found")
        void shouldReturnReviewWhenExists()
        {
            Long profileId = 1L;
            Long recipeId = 10L;
            Review expectedReview = new Review();

            when(reviewRepository.findByProfile_IdAndRecipe_Id(profileId, recipeId))
                    .thenReturn(Optional.of(expectedReview));

            Optional<Review> result = reviewService.getReviewByProfileAndRecipe(profileId, recipeId);

            assertThat(result).isPresent().contains(expectedReview);
            verify(reviewRepository, times(1)).findByProfile_IdAndRecipe_Id(profileId, recipeId);
        }

        @Test
        @DisplayName("Should return empty Optional when review not found")
        void shouldReturnEmptyWhenNotFound()
        {
            Long profileId = 1L;
            Long recipeId = 99L;

            when(reviewRepository.findByProfile_IdAndRecipe_Id(profileId, recipeId))
                    .thenReturn(Optional.empty());

            Optional<Review> result = reviewService.getReviewByProfileAndRecipe(profileId, recipeId);

            assertThat(result).isEmpty();
            verify(reviewRepository, times(1)).findByProfile_IdAndRecipe_Id(profileId, recipeId);
        }
    }

    @Nested
    @DisplayName("getReviewsByRecipeId Tests")
    class GetReviewsByRecipeIdTests
    {

        @Test
        @DisplayName("Should return list of reviews ordered by creation date descending")
        void shouldReturnReviewsForRecipe()
        {
            Long recipeId = 10L;
            List<Review> expectedReviews = List.of(new Review(), new Review());

            when(reviewRepository.findByRecipe_IdOrderByCreatedAtDesc(recipeId))
                    .thenReturn(expectedReviews);

            List<Review> results = reviewService.getReviewsByRecipeId(recipeId);

            assertThat(results).hasSize(2).isEqualTo(expectedReviews);
            verify(reviewRepository, times(1)).findByRecipe_IdOrderByCreatedAtDesc(recipeId);
        }
    }

    @Nested
    @DisplayName("getAverageRating & getReviewCount Tests")
    class MetricsTests
    {

        @Test
        @DisplayName("Should return average rating for a recipe")
        void shouldReturnAverageRating()
        {
            Long recipeId = 10L;
            when(reviewRepository.findAverageRatingByRecipeId(recipeId)).thenReturn(4.5);

            double result = reviewService.getAverageRating(recipeId);

            assertThat(result).isEqualTo(4.5);
            verify(reviewRepository, times(1)).findAverageRatingByRecipeId(recipeId);
        }

        @Test
        @DisplayName("Should return total review count for a recipe")
        void shouldReturnReviewCount()
        {
            Long recipeId = 10L;
            when(reviewRepository.countByRecipe_Id(recipeId)).thenReturn(12L);

            long result = reviewService.getReviewCount(recipeId);

            assertThat(result).isEqualTo(12L);
            verify(reviewRepository, times(1)).countByRecipe_Id(recipeId);
        }

        @Test
        @DisplayName("Should return true when profile has reviewed the recipe")
        void shouldReturnTrueWhenHasReviewed()
        {
            Long profileId = 1L;
            Long recipeId = 10L;
            when(reviewRepository.existsByProfile_IdAndRecipe_Id(profileId, recipeId)).thenReturn(true);

            boolean result = reviewService.hasReviewed(profileId, recipeId);

            assertThat(result).isTrue();
            verify(reviewRepository, times(1)).existsByProfile_IdAndRecipe_Id(profileId, recipeId);
        }
    }

    @Nested
    @DisplayName("upsertReview Tests")
    class UpsertReviewTests
    {

        @Test
        @DisplayName("Should call repository upsert with trimmed comment and correct IDs")
        void shouldUpsertValidReview()
        {
            Profile profile = mock(Profile.class);
            Recipe recipe = mock(Recipe.class);

            when(profile.getId()).thenReturn(1L);
            when(recipe.getId()).thenReturn(10L);

            Review review = new Review();
            review.setProfile(profile);
            review.setRecipe(recipe);
            review.setComment("  Great recipe!  ");
            review.setRating(5);

            reviewService.upsertReview(review);

            verify(reviewRepository, times(1)).upsert(1L, 10L, "Great recipe!", 5);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when profile is null")
        void shouldThrowExceptionWhenProfileIsNull()
        {
            Recipe recipe = mock(Recipe.class);

            Review review = new Review();
            review.setProfile(null);
            review.setRecipe(recipe);

            assertThatThrownBy(() -> reviewService.upsertReview(review))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Review must have an existing profile");

            verifyNoInteractions(reviewRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when profile ID is null")
        void shouldThrowExceptionWhenProfileIdIsNull()
        {
            Profile profile = mock(Profile.class);
            when(profile.getId()).thenReturn(null);

            Review review = new Review();
            review.setProfile(profile);

            assertThatThrownBy(() -> reviewService.upsertReview(review))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Review must have an existing profile");

            verifyNoInteractions(reviewRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when recipe is null")
        void shouldThrowExceptionWhenRecipeIsNull()
        {
            Profile profile = mock(Profile.class);
            when(profile.getId()).thenReturn(1L);

            Review review = new Review();
            review.setProfile(profile);
            review.setRecipe(null);

            assertThatThrownBy(() -> reviewService.upsertReview(review))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Review must have an existing recipe");

            verifyNoInteractions(reviewRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when recipe ID is null")
        void shouldThrowExceptionWhenRecipeIdIsNull()
        {
            Profile profile = mock(Profile.class);
            Recipe recipe = mock(Recipe.class);

            when(profile.getId()).thenReturn(1L);
            when(recipe.getId()).thenReturn(null);

            Review review = new Review();
            review.setProfile(profile);
            review.setRecipe(recipe);

            assertThatThrownBy(() -> reviewService.upsertReview(review))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Review must have an existing recipe");

            verifyNoInteractions(reviewRepository);
        }
    }
}