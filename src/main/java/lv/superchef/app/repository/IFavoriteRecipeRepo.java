package lv.superchef.app.repository;

import lv.superchef.app.model.FavoriteRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IFavoriteRecipeRepo extends JpaRepository<FavoriteRecipe, Long> {
    boolean existsByAppUser_IdAndRecipe_Id(Long userId, Long recipeId);

    Optional<FavoriteRecipe> findByAppUser_IdAndRecipe_Id(Long userId, Long recipeId);

    List<FavoriteRecipe> findAllByAppUser_Id(Long userId);

    long deleteByAppUser_IdAndRecipe_Id(Long userId);

    List<FavoriteRecipe> findAllByProfileId(Long profileId);
}
