package lv.superchef.app.service;

import jakarta.validation.Valid;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.model.Recipe;

import java.util.List;

public interface IRecipeService {
    Recipe createRecipe(@Valid RecipeCreateDTO dto);

    List<Recipe> searchRecipes(String keyword, String category, String difficulty, Integer maxCalories,
                               Integer maxPrepTime, Integer maxCookTime, Integer limit);

    List<Recipe> searchRecipes(String keyword, String category, String difficulty, Integer maxCalories,
                               Integer maxPrepTime, Integer maxCookTime);

    List<Recipe> getAllRecipes();

    Recipe getRecipeById(Long id);

    Recipe updateRecipe(Long id, @Valid RecipeCreateDTO dto);

    void deleteRecipe(Long id);

    List<Recipe> getRecipesByFollowedProfilesForUser(Long userId);

}
