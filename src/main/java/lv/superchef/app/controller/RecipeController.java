package lv.superchef.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.dto.ReviewFormDTO;
import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.model.Review;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    private final IRecipeService recipeService;
    private final IFavoriteRecipeService favoriteRecipeService;
    private final IImageStorageService imageStorageService;
    private final IReviewService reviewService;
    private final IProfileService profileService;

    public RecipeController(IRecipeService recipeService, IFavoriteRecipeService favoriteRecipeService, IImageStorageService imageStorageService, IReviewService reviewService, IProfileService profileService) {
        this.recipeService = recipeService;
        this.favoriteRecipeService = favoriteRecipeService;
        this.imageStorageService = imageStorageService;
        this.reviewService = reviewService;
        this.profileService = profileService;

    }

    @GetMapping
    public String listOfRecipes(@AuthenticationPrincipal AppUserDetails userDetails, @RequestParam(required = false) String query, @RequestParam(required = false) String category, @RequestParam(required = false) String difficulty, @RequestParam(required = false) Integer maxCalories, @RequestParam(required = false) Integer maxPrepTime, @RequestParam(required = false) Integer maxCookTime, Model model) {
        List<Recipe> recipes = recipeService.searchRecipes(query, category, difficulty, maxCalories, maxPrepTime, maxCookTime);

        model.addAttribute("recipes", recipes);
        addFavoriteState(userDetails, model);

        return "recipe/list";
    }

    @GetMapping("/{id}")
    public String recipeDetails(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails userDetails, HttpServletRequest request, Model model) {
        Recipe recipe = recipeService.getRecipeById(id);

        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        model.addAttribute("recipe", recipe);
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("reviews", reviewService.getReviewsByRecipeId(id));
        model.addAttribute("averageRating", reviewService.getAverageRating(id));
        model.addAttribute("reviewCount", reviewService.getReviewCount(id));

        Optional<Review> existingReview = Optional.empty();

        if (userDetails != null) {
            Profile profile = profileService
                    .getProfileByUserId(userDetails.getUserId())
                    .orElseThrow(() -> new IllegalStateException("Profile not found for user ID: " + userDetails.getUserId()));

            existingReview = reviewService.getReviewByProfileAndRecipe(profile.getId(), id);
        }

        if (!model.containsAttribute("reviewForm")) {
            ReviewFormDTO reviewForm = new ReviewFormDTO();

            existingReview.ifPresent(review -> {
                reviewForm.setRating(review.getRating());
                reviewForm.setComment(review.getComment());
            });

            model.addAttribute("reviewForm", reviewForm);
        }

        model.addAttribute("editingReview", existingReview.isPresent());

        addFavoriteState(userDetails, model);

        return "recipe/details";
    }

    @GetMapping("/create")
    public String showCreateRecipePage(@AuthenticationPrincipal AppUserDetails userDetails, Model model) {
        RecipeCreateDTO createRecipeDto = new RecipeCreateDTO();
        createRecipeDto.setIngredients(new ArrayList<>(List.of(new IngredientInputDTO())));
        createRecipeDto.setSteps(new ArrayList<>(List.of("")));
        model.addAttribute("loggedIn", userDetails != null);
        model.addAttribute("activePage", "createRecipe");
        model.addAttribute("createRecipeDto", createRecipeDto);
        model.addAttribute("ingredientUnits", IngredientUnit.values());

        return "recipe/create";
    }

    @PostMapping
    public String handleCreateRecipe(@Valid @ModelAttribute RecipeCreateDTO createRecipeDto, @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {

        String imageUrl = imageStorageService.storeCoverImage(coverImage);

        createRecipeDto.setImageUrl(imageUrl);

        recipeService.createRecipe(createRecipeDto);

        return "redirect:/recipes?success=true";
    }

    private void addFavoriteState(AppUserDetails userDetails, Model model) {
        boolean loggedIn = userDetails != null;

        Set<Long> favoriteRecipeIds;

        if (loggedIn) {
            favoriteRecipeIds = favoriteRecipeService.getFavoriteRecipeIdsByUserId(userDetails.getUserId());
        } else {
            favoriteRecipeIds = Set.of();
        }

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("favoriteRecipeIds", favoriteRecipeIds);
    }
}
