package lv.superchef.app.repo;

import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.enums.Role;
import lv.superchef.app.model.*;
import lv.superchef.app.repository.IFavoriteRecipeRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")

public class IFavoriteRecipeRepoTest {
    @Autowired
    IFavoriteRecipeRepo favoriteRecipeRepo;

    @Autowired
    TestEntityManager entityManager;

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

        Recipe recipe = createTestRecipe("Creamy Garlic Pasta");
        Recipe recipe2 = createTestRecipe("Creamy Garlic Wrap");
        entityManager.persistAndFlush(recipe);
        entityManager.persistAndFlush(recipe2);

        FavoriteRecipe favoriteRecipe = new FavoriteRecipe(recipe, profile);
        entityManager.persistAndFlush(favoriteRecipe);

        long deletedRows = favoriteRecipeRepo.deleteByProfile_IdAndRecipe_Id(profile.getId(), recipe2.getId());

        assertEquals(0, deletedRows);
        assertTrue(favoriteRecipeRepo.existsById(favoriteRecipe.getId()));
    }


    private Recipe createTestRecipe(String title){
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredientName("fettucine");
        recipeIngredient.setQuantity(160.0);
        recipeIngredient.setUnit(IngredientUnit.G);

        RecipeStep recipeStep = new RecipeStep();
        recipeStep.setStepNumber(1);
        recipeStep.setInstruction("Cook the pasta until al dente.");

        Recipe recipe = new Recipe();
        recipe.setTitle(title);
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
