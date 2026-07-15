package lv.superchef.app.service;

import jakarta.validation.Valid;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.model.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRecipeService
{
    public Recipe createRecipe(@Valid RecipeCreateDTO dto, MultipartFile coverImage);

    public List<Recipe> searchRecipes(String keyword, String category, String difficulty, Integer maxCalories,
                                      Integer maxPrepTime, Integer maxCookTime);
    public List<Recipe> getAllRecipes();
    public Recipe getRecipeById(Long id);

    public Recipe updateRecipe(Long id, @Valid RecipeCreateDTO dto);

    public void deleteRecipe(Long id);

}
