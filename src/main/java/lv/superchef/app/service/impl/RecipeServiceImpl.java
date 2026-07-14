package lv.superchef.app.service.impl;

import lv.superchef.app.model.Recipe;
import lv.superchef.app.repository.IRecipeRepo;
import lv.superchef.app.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeServiceImpl implements IRecipeService {

    @Autowired
    private IRecipeRepo recipeRepo;

    @Override
    public List<Recipe> searchRecipes(String keyword) {
        if(keyword == null || keyword.isBlank()){
            throw new IllegalArgumentException("Keyword is null");
        }

        return recipeRepo.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }
}
