package lv.superchef.app.service;

import lv.superchef.app.model.FavoriteRecipe;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;

import java.util.List;
import java.util.Set;

public interface IFavoriteRecipeService {
    boolean addToFavorites(Recipe recipe, Profile profile);

    boolean removeFromFavorites(Recipe recipe, Profile profile);

    List<FavoriteRecipe> getAllFavoriteRecipesByUserId(Long userId);

    Set<Long> getFavoriteRecipeIdsByUserId(Long userId);
}
