package lv.superchef.app.service;

import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.model.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRecipeService {
    public List<Recipe> searchRecipes(String keyword, String category, String difficulty, Integer maxCalories,
                                      Integer maxPrepTime, Integer maxCookTime);

    public Recipe getRecipeById(Long id);

    public Recipe createRecipe(RecipeCreateDTO dto, MultipartFile coverImage);
}
