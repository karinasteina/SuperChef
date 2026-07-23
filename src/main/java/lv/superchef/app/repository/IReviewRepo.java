package lv.superchef.app.repository;

import lv.superchef.app.model.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IReviewRepo extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = "profile")
    List<Review> findByRecipe_IdOrderByCreatedAtDesc(Long recipeId);

    long countByRecipe_Id(Long recipeId);

    boolean existsByProfile_IdAndRecipe_Id(Long profileId, Long recipeId);

    Optional<Review> findByProfile_IdAndRecipe_Id(Long profileId, Long recipeId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            INSERT INTO review (
                profile_id,
                recipe_id,
                comment,
                rating,
                created_at,
                updated_at
            )
            VALUES (
                :profileId,
                :recipeId,
                :comment,
                :rating,
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON CONFLICT (profile_id, recipe_id)
            DO UPDATE SET
                comment = excluded.comment,
                rating = excluded.rating,
                updated_at = CURRENT_TIMESTAMP
            """, nativeQuery = true)
    int upsert(@Param("profileId") Long profileId, @Param("recipeId") Long recipeId, @Param("comment") String comment, @Param("rating") Integer rating);

    @Query("""
            select coalesce(avg(r.rating), 0.0)
            from Review r
            where r.recipe.id = :recipeId
            """)
    double findAverageRatingByRecipeId(@Param("recipeId") Long recipeId);
}