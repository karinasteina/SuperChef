package lv.superchef.app.service.impl;

import lv.superchef.app.model.FavoriteRecipe;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.repository.IFavoriteRecipeRepo;
import lv.superchef.app.repository.IProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FavoriteRecipeService {
    @Autowired
    private IFavoriteRecipeRepo favoriteRecipeRepo;
    @Autowired
    private IProfileRepo profileRepository;

    public void addToFavorites(Recipe recipe, Profile profile) {
        FavoriteRecipe favoriteRecipe = new FavoriteRecipe(recipe, profile);
        favoriteRecipeRepo.save(favoriteRecipe);

    }

    public void removeFromFavorites(Recipe recipe, Profile profile) {
        FavoriteRecipe favoriteRecipe = new FavoriteRecipe(recipe, profile);
        favoriteRecipeRepo.delete(favoriteRecipe);
    }


    public List<FavoriteRecipe> getAllFavoriteRecipes() {
        return favoriteRecipeRepo.findAll();
    }

    public List<FavoriteRecipe> getAllFavoriteRecipesByProfile(Profile profile) {
        return favoriteRecipeRepo.findAllByProfileId(profile.getId());
    }

    public List<FavoriteRecipe> getAllFavoriteRecipesByUserId(Long userId) {
        Profile profile = profileRepository.getProfileByUserId(userId);

        return getAllFavoriteRecipesByProfile(profile);
    }
}
