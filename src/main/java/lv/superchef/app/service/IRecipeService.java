package lv.superchef.app.service;

import lv.superchef.app.model.Recipe;

import java.util.List;

public interface IRecipeService {
    public List<Recipe> searchRecipes(String keyword);
}
