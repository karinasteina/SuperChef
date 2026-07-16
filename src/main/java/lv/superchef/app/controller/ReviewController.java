package lv.superchef.app.controller;

import jakarta.validation.Valid;
import lv.superchef.app.dto.ReviewFormDTO;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.model.Review;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IProfileService;
import lv.superchef.app.service.IRecipeService;
import lv.superchef.app.service.IReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/recipes")
public class ReviewController {

    private final IReviewService reviewService;
    private final IRecipeService recipeService;
    private final IProfileService profileService;

    public ReviewController(IReviewService reviewService, IRecipeService recipeService, IProfileService profileService) {
        this.reviewService = reviewService;
        this.recipeService = recipeService;
        this.profileService = profileService;
    }

    @PostMapping("/{recipeId}/reviews")
    public String saveReview(@PathVariable Long recipeId, @AuthenticationPrincipal AppUserDetails userDetails, @Valid @ModelAttribute("reviewForm") ReviewFormDTO reviewForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reviewForm", bindingResult);

            redirectAttributes.addFlashAttribute("reviewForm", reviewForm);

            return "redirect:/recipes/" + recipeId;
        }

        Profile profile = profileService
                .getProfileByUserId(userDetails.getUserId())
                .orElseThrow(() -> new IllegalStateException("Profile not found for user ID: " + userDetails.getUserId()));

        Recipe recipe = recipeService.getRecipeById(recipeId);

        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        Review review = new Review();
        review.setProfile(profile);
        review.setRecipe(recipe);
        review.setRating(reviewForm.getRating());
        review.setComment(reviewForm
                .getComment()
                .trim());

        reviewService.upsertReview(review);

        redirectAttributes.addFlashAttribute("reviewSuccess", "Your review was saved successfully.");

        return "redirect:/recipes/" + recipeId;
    }
}