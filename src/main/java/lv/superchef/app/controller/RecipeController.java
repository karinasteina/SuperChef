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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
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

        String currentUrl = request.getRequestURL().toString();

        model.addAttribute("recipe", recipe);
        model.addAttribute("currentUrl", currentUrl);
        model.addAttribute("absoluteImageUrl", URI.create(currentUrl).resolve(recipe.getImageUrl()).toString());
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
        model.addAttribute("authorName", userDetails != null ? userDetails.getUsername() : "Guest");

        return "recipe/create";
    }

    @PostMapping
    public String handleCreateRecipe(
            @AuthenticationPrincipal AppUserDetails userDetails,
            @Valid @ModelAttribute("createRecipeDto") RecipeCreateDTO createRecipeDto,
            BindingResult bindingResult,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            Model model) {

        if (coverImage == null || coverImage.isEmpty()) {
            bindingResult.reject("coverImage", "Cover image is required");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("loggedIn", userDetails != null);
            model.addAttribute("activePage", "createRecipe");
            model.addAttribute("ingredientUnits", IngredientUnit.values());
            model.addAttribute("authorName", userDetails != null ? userDetails.getUsername() : "Guest");
            return "recipe/create";
        }

        String imageUrl = imageStorageService.storeCoverImage(coverImage);

        createRecipeDto.setImageUrl(imageUrl);

        if (userDetails != null) {
            createRecipeDto.setAuthorId(userDetails.getUserId());
        }

        recipeService.createRecipe(createRecipeDto);

        return "redirect:/recipes?success=true";
    }

    @GetMapping("/{id}/edit")
    public String showEditRecipePage(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails userDetails, Model model) {
        Recipe recipe = recipeService.getRecipeById(id);

        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        // Convert Recipe to RecipeCreateDTO for form binding
        RecipeCreateDTO editDto = new RecipeCreateDTO();
        editDto.setTitle(recipe.getTitle());
        editDto.setDescription(recipe.getDescription());
        editDto.setImageUrl(recipe.getImageUrl());
        editDto.setCalories(recipe.getCalories());
        editDto.setPreparationTime(recipe.getPreparationTime());
        editDto.setCookingTime(recipe.getCookingTime());
        editDto.setDifficulty(recipe.getDifficulty());
        editDto.setCategory(recipe.getCategory());

        // Map ingredients
        List<IngredientInputDTO> ingredientDtos = new ArrayList<>();
        for (var ingredient : recipe.getIngredients()) {
            IngredientInputDTO dto = new IngredientInputDTO();
            dto.setName(ingredient.getIngredientName());
            dto.setQuantity(ingredient.getQuantity());
            dto.setUnit(ingredient.getUnit());
            ingredientDtos.add(dto);
        }
        editDto.setIngredients(ingredientDtos);

        // Map steps
        List<String> steps = new ArrayList<>();
        for (var step : recipe.getSteps()) {
            steps.add(step.getInstruction());
        }
        editDto.setSteps(steps);

        model.addAttribute("recipeId", id);
        model.addAttribute("loggedIn", userDetails != null);
        model.addAttribute("activePage", "editRecipe");
        model.addAttribute("createRecipeDto", editDto);
        model.addAttribute("ingredientUnits", IngredientUnit.values());
        model.addAttribute("isEditMode", true);
        model.addAttribute("authorName", recipe.getAuthor() != null ? recipe.getAuthor().getDisplayName() : "Unknown");

        return "recipe/edit";
    }

    @PostMapping("/{id}/update")
    public String handleUpdateRecipe(
            @PathVariable Long id,
            @Valid @ModelAttribute RecipeCreateDTO createRecipeDto,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage, RedirectAttributes redirectAttributes) {

        // Only store new image if one was uploaded
        if (coverImage != null && !coverImage.isEmpty()) {
            String imageUrl = imageStorageService.storeCoverImage(coverImage);
            createRecipeDto.setImageUrl(imageUrl);
        } else {
            // Keep existing image
            Recipe existingRecipe = recipeService.getRecipeById(id);
            createRecipeDto.setImageUrl(existingRecipe.getImageUrl());
        }

        recipeService.updateRecipe(id, createRecipeDto);
        redirectAttributes.addFlashAttribute(
                "message", "Recipe updated successfully.");
        redirectAttributes.addFlashAttribute("status", "success");
        return "redirect:/recipes";
    }

    @PostMapping("/{id}/delete")
    public String handleDeleteRecipe(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        recipeService.deleteRecipe(id);
        redirectAttributes.addFlashAttribute(
                "message", "Recipe deleted successfully.");
        redirectAttributes.addFlashAttribute("status", "success");
        return "redirect:/recipes";
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
