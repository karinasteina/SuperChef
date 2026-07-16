package lv.superchef.app.service.impl;

import lv.superchef.app.model.FavoriteRecipe;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.repository.IFavoriteRecipeRepo;
import lv.superchef.app.repository.IProfileRepo;
import lv.superchef.app.service.IFavoriteRecipeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteRecipeService implements IFavoriteRecipeService {

    private final IFavoriteRecipeRepo favoriteRecipeRepo;
    private final IProfileRepo profileRepository;

    public FavoriteRecipeService(IFavoriteRecipeRepo favoriteRecipeRepo, IProfileRepo profileRepository) {
        this.favoriteRecipeRepo = favoriteRecipeRepo;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public boolean addToFavorites(Recipe recipe, Profile profile) {
        validate(recipe, profile);

        int inserted = favoriteRecipeRepo.insertIfAbsent(profile.getId(), recipe.getId());

        return inserted > 0;
    }

    @Transactional
    public boolean removeFromFavorites(Recipe recipe, Profile profile) {
        validate(recipe, profile);

        long deleted = favoriteRecipeRepo.deleteByProfile_IdAndRecipe_Id(profile.getId(), recipe.getId());

        return deleted > 0;
    }

    @Transactional(readOnly = true)
    public List<FavoriteRecipe> getAllFavoriteRecipesByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }

        Profile profile = profileRepository
                .findByAppUser_Id(userId)
                .orElse(null);
        if (profile == null) {
            return List.of();
        }
        return favoriteRecipeRepo.findAllByProfile_Id(profile.getId());

    }

    @Transactional(readOnly = true)
    public Set<Long> getFavoriteRecipeIdsByUserId(Long userId) {
        if (userId == null) {
            return Set.of();
        }
        Profile profile = profileRepository
                .findByAppUser_Id(userId)
                .orElse(null);
        if (profile == null) {
            return Set.of();
        }

        return getAllFavoriteRecipesByUserId(userId)
                .stream()
                .map(FavoriteRecipe::getRecipe)
                .map(Recipe::getId)
                .collect(Collectors.toSet());
    }

    private void validate(Recipe recipe, Profile profile) {
        if (recipe == null || recipe.getId() == null) {
            throw new IllegalArgumentException("Recipe must be persisted");
        }

        if (profile == null || profile.getId() == null) {
            throw new IllegalArgumentException("Profile must be persisted");
        }
    }

}
