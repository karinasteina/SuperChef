package lv.superchef.app.repository;

import lv.superchef.app.model.FavoriteRecipe;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFavoriteRecipeRepo extends JpaRepository<FavoriteRecipe, Long> {
    @Modifying
    @Query(value = """
            INSERT INTO favorite_recipe (
                favorite_recipe_id,
                profile_id,
                recipe_id
            )
            VALUES (
                NULL,
                :profileId,
                :recipeId
            )
            ON CONFLICT (profile_id, recipe_id)
            DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(@Param("profileId") Long profileId, @Param("recipeId") Long recipeId);

    @EntityGraph(attributePaths = "recipe")
    List<FavoriteRecipe> findAllByProfile_Id(Long profileId);

    long deleteByProfile_IdAndRecipe_Id(Long profileId, Long recipeId);
}