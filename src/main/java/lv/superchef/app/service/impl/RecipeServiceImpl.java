package lv.superchef.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.repository.IRecipeRepo;
import lv.superchef.app.repository.RecipeSpecifications;
import lv.superchef.app.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeServiceImpl implements IRecipeService {

    @Autowired
    private IRecipeRepo recipeRepo;


    @Override
    public List<Recipe> searchRecipes(String keyword, String category, String difficulty, Integer maxCalories, Integer maxPrepTime, Integer maxCookTime) {
        return recipeRepo.findAll(RecipeSpecifications.recipeByFilter(keyword, category,
                difficulty, maxCalories, maxPrepTime, maxCookTime));
    }

    @Override
    public Recipe getRecipeById(Long id) {
        return recipeRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Recipe not found: " + id));
    }
}
