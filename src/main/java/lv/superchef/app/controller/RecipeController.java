package lv.superchef.app.controller;

import jakarta.validation.Valid;
import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IFavoriteRecipeService;
import lv.superchef.app.service.IImageStorageService;
import lv.superchef.app.service.IRecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    private final IRecipeService recipeService;
    private final IFavoriteRecipeService favoriteRecipeService;
    private final IImageStorageService imageStorageService;

    public RecipeController(IRecipeService recipeService, IFavoriteRecipeService favoriteRecipeService, IImageStorageService imageStorageService)
    {
        this.recipeService = recipeService;
        this.favoriteRecipeService = favoriteRecipeService;
        this.imageStorageService = imageStorageService;
    }

    @GetMapping
    public String listOfRecipes(@AuthenticationPrincipal AppUserDetails userDetails, @RequestParam(required = false) String query, @RequestParam(required = false) String category, @RequestParam(required = false) String difficulty, @RequestParam(required = false) Integer maxCalories, @RequestParam(required = false) Integer maxPrepTime, @RequestParam(required = false) Integer maxCookTime, Model model) {
        List<Recipe> recipes = recipeService.searchRecipes(query, category, difficulty, maxCalories, maxPrepTime, maxCookTime);

        model.addAttribute("recipes", recipes);
        addFavoriteState(userDetails, model);

        return "recipe/list";
    }

    @GetMapping("/{id}")
    public String recipeDetails(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails userDetails, Model model) {
        Recipe recipe = recipeService.getRecipeById(id);

        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        model.addAttribute("recipe", recipe);
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
    public String handleCreateRecipe(
            @Valid @ModelAttribute RecipeCreateDTO createRecipeDto,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {

        String imageUrl = imageStorageService.storeCoverImage(coverImage);

        createRecipeDto.setImageUrl(imageUrl);

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

        return "recipe/edit";
    }

    @PostMapping("/{id}/update")
    public String handleUpdateRecipe(
            @PathVariable Long id,
            @Valid @ModelAttribute RecipeCreateDTO createRecipeDto,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {

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

        return "redirect:/recipes?updated=true";
    }

    @PostMapping("/{id}/delete")
    public String handleDeleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return "redirect:/recipes?deleted=true";
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
