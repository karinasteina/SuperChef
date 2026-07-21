package lv.superchef.app.service.impl;

import lv.superchef.app.model.FavoriteRecipe;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.repository.IFavoriteRecipeRepo;
import lv.superchef.app.repository.IProfileRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Favorite Recipe Service Unit Tests")
class FavoriteRecipeServiceTest {

    @Mock
    private IFavoriteRecipeRepo favoriteRecipeRepo;

    @Mock
    private IProfileRepo profileRepository;

    @InjectMocks
    private FavoriteRecipeService favoriteRecipeService;

    @Test
    @DisplayName("Get Favorite IDs: Should successfully map and return core recipe IDs rather than intermediary table row IDs")
    void getFavoriteRecipeIdsReturnsRecipeIdsInsteadOfFavoriteRowIds() {
        Long userId = 11L;
        Long profileId = 22L;
        Long recipeId = 42L;

        Profile profile = org.mockito.Mockito.mock(Profile.class);
        FavoriteRecipe favorite = org.mockito.Mockito.mock(FavoriteRecipe.class);
        Recipe recipe = org.mockito.Mockito.mock(Recipe.class);

        when(profile.getId()).thenReturn(profileId);
        when(favorite.getRecipe()).thenReturn(recipe);
        when(recipe.getId()).thenReturn(recipeId);
        when(profileRepository.findByAppUser_Id(userId)).thenReturn(Optional.of(profile));
        when(favoriteRecipeRepo.findAllByProfile_Id(profileId)).thenReturn(List.of(favorite));

        Set<Long> favoriteRecipeIds = favoriteRecipeService.getFavoriteRecipeIdsByUserId(userId);

        assertThat(favoriteRecipeIds).containsExactly(recipeId);
    }
}
