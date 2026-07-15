package lv.superchef.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.model.RecipeIngredient;
import lv.superchef.app.model.RecipeStep;
import lv.superchef.app.repository.IRecipeRepo;
import lv.superchef.app.repository.RecipeSpecifications;
import lv.superchef.app.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
public class RecipeServiceImpl implements IRecipeService
{
    @Autowired
    private IRecipeRepo recipeRepo;

    @Override
    @Transactional
    public Recipe createRecipe(@Valid RecipeCreateDTO dto)
    {
        Recipe recipe = new Recipe();
        mapDtoToRecipe(dto, recipe);
        return recipeRepo.save(recipe);
    }

    @Override
    public List<Recipe> searchRecipes(String keyword, String category, String difficulty, Integer maxCalories, Integer maxPrepTime, Integer maxCookTime) {
        return recipeRepo.findAll(RecipeSpecifications.recipeByFilter(keyword, category,
                difficulty, maxCalories, maxPrepTime, maxCookTime));
    }

    @Override
    public List<Recipe> getAllRecipes()
    {
        return recipeRepo.findAll();
    }

    @Override
    public Recipe getRecipeById(Long id) {
        return recipeRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Recipe not found: " + id));
    }

    @Override
    @Transactional
    public Recipe updateRecipe(Long id, @Valid RecipeCreateDTO dto)
    {
        Recipe recipe = getRecipeById(id);
        mapDtoToRecipe(dto, recipe);
        recipe.setUpdatedAt(LocalDateTime.now());
        return recipeRepo.save(recipe);
    }

    @Override
    @Transactional
    public void deleteRecipe(Long id)
    {
        Recipe recipe = getRecipeById(id);
        recipeRepo.delete(recipe);
    }

    private void mapDtoToRecipe(RecipeCreateDTO dto, Recipe recipe)
    {
        recipe.setTitle(dto.getTitle());
        recipe.setDescription(dto.getDescription());
        recipe.setImageUrl(dto.getImageUrl());
        recipe.setCalories(dto.getCalories());
        recipe.setPreparationTime(dto.getPreparationTime());
        recipe.setCookingTime(dto.getCookingTime());
        recipe.setDifficulty(dto.getDifficulty());
        recipe.setCategory(dto.getCategory());

        recipe.getIngredients().clear();
        if(dto.getIngredients() != null)
        {
            for (IngredientInputDTO ingDto : dto.getIngredients())
            {
                RecipeIngredient ingredient = new RecipeIngredient();
                ingredient.setIngredientName(ingDto.getName());
                ingredient.setQuantity(ingDto.getQuantity());
                ingredient.setUnit(ingDto.getUnit());
                recipe.getIngredients().add(ingredient);
            }
        }

        recipe.getSteps().clear();
        if(dto.getSteps() != null)
        {
            int currentStepNum = 1;
            for (String stepInstruction : dto.getSteps())
            {
                RecipeStep step = new RecipeStep();
                step.setStepNumber(currentStepNum++);
                step.setInstruction(stepInstruction);
                recipe.getSteps().add(step);
            }
        }
    }

}
