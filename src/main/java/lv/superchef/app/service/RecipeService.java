package lv.superchef.app.service;

import jakarta.validation.Valid;
import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.model.RecipeIngredient;
import lv.superchef.app.model.RecipeStep;
import lv.superchef.app.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;

    public Recipe createRecipe(@Valid RecipeCreateDTO dto) {
        Recipe recipe = new Recipe();
        recipe.setTitle(dto.getTitle());
        recipe.setDescription(dto.getDescription());
        recipe.setImageUrl(dto.getImageUrl());
        recipe.setCalories(dto.getCalories());
        recipe.setPreparationTime(dto.getPreparationTime());
        recipe.setCookingTime(dto.getCookingTime());
        recipe.setDifficulty(dto.getDifficulty());
        recipe.setCategory(dto.getCategory());

        if (dto.getIngredients() != null) {
            for (IngredientInputDTO ingDto : dto.getIngredients()) {
                RecipeIngredient ingredient = new RecipeIngredient();
                ingredient.setIngredientName(ingDto.getName());
                ingredient.setQuantity(ingDto.getQuantity());
                ingredient.setUnit(ingDto.getUnit());

                recipe
                        .getIngredients()
                        .add(ingredient);
            }
        }
        if (dto.getSteps() != null) {
            int currentStepNum = 1;
            for (String stepInstruction : dto.getSteps()) {
                RecipeStep step = new RecipeStep();
                step.setStepNumber(currentStepNum++);
                step.setInstruction(stepInstruction);

                recipe
                        .getSteps()
                        .add(step);
            }
        }

        return recipeRepository.save(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository
                .findById(id)
                .orElse(null);
    }
}
