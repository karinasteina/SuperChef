package lv.superchef.app.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lv.superchef.app.model.Review;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IReviewService {


    @Transactional(readOnly = true)
    double getAverageRating(Long recipeId);

    @Transactional(readOnly = true)
    long getReviewCount(Long recipeId);

    @Transactional(readOnly = true)
    boolean hasReviewed(Long profileId, Long recipeId);

    @Transactional
    void upsertReview(@NotNull @Valid Review review);

    @Transactional(readOnly = true)
    Optional<Review> getReviewByProfileAndRecipe(Long profileId, Long recipeId);

    List<Review> getReviewsByRecipeId(Long recipeId);


}
