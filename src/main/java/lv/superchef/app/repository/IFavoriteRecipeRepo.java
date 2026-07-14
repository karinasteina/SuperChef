package lv.superchef.app.repository;

import lv.superchef.app.model.FavoriteRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFavoriteRecipeRepo extends JpaRepository<FavoriteRecipe, Long> {

    boolean existsByProfile_IdAndRecipe_Id(Long profileId, Long recipeId);

    Optional<FavoriteRecipe> findByProfile_IdAndRecipe_Id(Long profileId, Long recipeId);

    List<FavoriteRecipe> findAllByProfile_Id(Long profileId);

    long deleteByProfile_IdAndRecipe_Id(Long profileId, Long recipeId);
}