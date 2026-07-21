package lv.superchef.app.repo;

import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.enums.Role;
import lv.superchef.app.model.*;
import lv.superchef.app.repository.IFavoriteRecipeRepo;
import lv.superchef.app.service.IRecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static lv.superchef.app.config.TempData.RECIPE_DATA;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class IFavoriteRecipeRepoTest {
    @Autowired
    IFavoriteRecipeRepo favoriteRecipeRepo;

    @Autowired
    TestEntityManager entityManager;


   /* public interface IFavoriteRecipeRepo extends JpaRepository<FavoriteRecipe, Long> {

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
        List<FavoriteRecipe> findAllByProfile_Id(Long profileId); */

    @Test
    void deleteByProfileIdAndRecipeIdWithExistingIdsDeletesCorrectRow(){
        AppUser appUser = new AppUser("testusername", "testpassword123","test@test.com", Role.ROLE_USER);
        entityManager.persistAndFlush(appUser);

        Profile profile = new Profile(appUser.getUsername(), "New Test Chef Profile", "image.jpg", appUser);
        entityManager.persistAndFlush(profile);

        Recipe recipe = createTestRecipe("fettuccine");

        entityManager.persistAndFlush(recipe);

        FavoriteRecipe favoriteRecipe = new FavoriteRecipe(recipe, profile);
        entityManager.persistAndFlush(favoriteRecipe);

        long deletedRows = favoriteRecipeRepo.deleteByProfile_IdAndRecipe_Id(profile.getId(), recipe.getId());

        assertEquals(1, deletedRows);
        assertFalse(favoriteRecipeRepo.existsById(favoriteRecipe.getId()));

    }

    @Test
    void deleteByProfileIdAndRecipeIdWithNonExistingIdsDoesNothing(){
        long deletedRows = favoriteRecipeRepo.deleteByProfile_IdAndRecipe_Id(2L, 3L);

        assertEquals(0, deletedRows);

    }

    @Test
    void deleteByProfileIdAndRecipeIdWrongPairingDoesNothing(){
        AppUser appUser = new AppUser("testusername", "testpassword123","test@test.com", Role.ROLE_USER);
        entityManager.persistAndFlush(appUser);

        Profile profile = new Profile(appUser.getUsername(), "New Test Chef Profile", "image.jpg", appUser);
        entityManager.persistAndFlush(profile);

        Recipe recipe = createTestRecipe("fettuccine");
        Recipe recipe2 = createTestRecipe("fettuccineTest");
        entityManager.persistAndFlush(recipe);
        entityManager.persistAndFlush(recipe2);

        FavoriteRecipe favoriteRecipe = new FavoriteRecipe(recipe, profile);
        entityManager.persistAndFlush(favoriteRecipe);

        long deletedRows = favoriteRecipeRepo.deleteByProfile_IdAndRecipe_Id(profile.getId(), recipe2.getId());

        assertEquals(0, deletedRows);
        assertTrue(favoriteRecipeRepo.existsById(favoriteRecipe.getId()));
    }

    @Test
    void findAllByProfileIdReturnsAllFavoritesForAProfile(){
        AppUser appUser = new AppUser("testusername", "testpassword123","test@test.com", Role.ROLE_USER);
        entityManager.persistAndFlush(appUser);

        Profile profile = new Profile(appUser.getUsername(), "New Test Chef Profile", "image.jpg", appUser);
        entityManager.persistAndFlush(profile);

        Recipe recipe = createTestRecipe("fettuccine");
        Recipe recipe2 = createTestRecipe("fettuccineTest");

        entityManager.persistAndFlush(recipe);
        entityManager.persistAndFlush(recipe2);

        FavoriteRecipe favoriteRecipe = new FavoriteRecipe(recipe, profile);
        FavoriteRecipe favoriteRecipe2 = new FavoriteRecipe(recipe2, profile);

        entityManager.persistAndFlush(favoriteRecipe);
        entityManager.persistAndFlush(favoriteRecipe2);

        List<FavoriteRecipe> favorites = favoriteRecipeRepo.findAllByProfile_Id(profile.getId());
    }


    private Recipe createTestRecipe(String title){
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredientName(title);
        recipeIngredient.setQuantity(160.0);
        recipeIngredient.setUnit(IngredientUnit.G);

        RecipeStep recipeStep = new RecipeStep();
        recipeStep.setStepNumber(1);
        recipeStep.setInstruction("Cook the pasta until al dente.");

        Recipe recipe = new Recipe();
        recipe.setTitle("Creamy Garlic Pasta");
        recipe.setDescription("Silky pasta coated in a rich garlic and Parmesan cream sauce.");
        recipe.setImageUrl("/images/recipes/recipe-00.webp");
        recipe.setCalories(620);
        recipe.setPreparationTime(10);
        recipe.setCookingTime(20);
        recipe.setDifficulty("Easy");
        recipe.setCategory("Dinner");
        recipe.setIngredients(List.of(recipeIngredient));
        recipe.setSteps(List.of(recipeStep));

        return recipe;
    }


}
