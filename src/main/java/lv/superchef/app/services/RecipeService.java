package lv.superchef.app.services;

import lv.superchef.app.dtos.IngredientInputDTO;
import lv.superchef.app.dtos.RecipeCreateDTO;
import lv.superchef.app.models.Recipe;
import lv.superchef.app.models.RecipeIngredient;
import lv.superchef.app.models.RecipeStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RecipeService
{
    private final List<Recipe> mockDatabase = new ArrayList<>();

    private final AtomicLong recipeIdCounter = new AtomicLong(1);
    private final AtomicLong ingredientIdCounter = new AtomicLong(1);
    private final AtomicLong stepIdCounter = new AtomicLong(1);

    public Recipe createRecipe(RecipeCreateDTO dto)
    {
        Recipe recipe = new Recipe();
        recipe.setId(recipeIdCounter.getAndIncrement());
        recipe.setTitle(dto.getTitle());
        recipe.setDescription(dto.getDescription());
        recipe.setImageUrl(dto.getImageUrl());
        recipe.setCalories(dto.getCalories());
        recipe.setPreparationTime(dto.getPreparationTime());
        recipe.setCookingTime(dto.getCookingTime());
        recipe.setDifficulty(dto.getDifficulty());
        recipe.setCategory(dto.getCategory());

        if(dto.getIngredients() != null)
        {
            for (IngredientInputDTO ingDto : dto.getIngredients())
            {
                RecipeIngredient ingredient = new RecipeIngredient(
                        ingredientIdCounter.getAndIncrement(),
                        ingDto.getName(),
                        ingDto.getQuantity(),
                        ingDto.getUnit()
                );
                recipe.getIngredients().add(ingredient);
            }
        }
        if (dto.getSteps() != null) {
            int currentStepNum = 1;
            for (String stepInstruction : dto.getSteps()) {
                RecipeStep step = new RecipeStep(
                        stepIdCounter.getAndIncrement(),
                        currentStepNum++,
                        stepInstruction
                );
                recipe.getSteps().add(step);
            }
        }

        mockDatabase.add(recipe);
        return recipe;
    }

    public List<Recipe> getAllRecipes()
    {
        return mockDatabase;
    }
}
