package lv.superchef.app.service.impl;

import lv.superchef.app.model.Review;
import lv.superchef.app.repository.IReviewRepo;
import lv.superchef.app.service.IReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class ReviewServiceImpl implements IReviewService {

    private final IReviewRepo reviewRepository;

    public ReviewServiceImpl(IReviewRepo reviewRepository) {
        this.reviewRepository = reviewRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Review> getReviewByProfileAndRecipe(Long profileId, Long recipeId) {
        return reviewRepository.findByProfile_IdAndRecipe_Id(profileId, recipeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByRecipeId(Long recipeId) {
        return reviewRepository.findByRecipe_IdOrderByCreatedAtDesc(recipeId);
    }


    @Override
    @Transactional(readOnly = true)
    public double getAverageRating(Long recipeId) {
        return reviewRepository.findAverageRatingByRecipeId(recipeId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getReviewCount(Long recipeId) {
        return reviewRepository.countByRecipe_Id(recipeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasReviewed(Long profileId, Long recipeId) {
        return reviewRepository.existsByProfile_IdAndRecipe_Id(profileId, recipeId);
    }

    @Override
    @Transactional
    public void upsertReview(Review review) {
        validateReviewReferences(review);

        reviewRepository.upsert(
                review.getProfile().getId(),
                review.getRecipe().getId(),
                review.getComment().trim(),
                review.getRating()
        );
    }

    private void validateReviewReferences(Review review) {
        if (review.getProfile() == null || review
                .getProfile()
                .getId() == null) {
            throw new IllegalArgumentException("Review must have an existing profile");
        }

        if (review.getRecipe() == null || review
                .getRecipe()
                .getId() == null) {
            throw new IllegalArgumentException("Review must have an existing recipe");
        }
    }
}