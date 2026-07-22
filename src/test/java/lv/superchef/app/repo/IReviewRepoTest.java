package lv.superchef.app.repo;

import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.enums.Role;
import lv.superchef.app.model.*;
import lv.superchef.app.repository.IReviewRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class IReviewRepoTest {
    @Autowired
    IReviewRepo reviewRepo;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void findAverageRatingByRecipeIdReturnsCorrectAverage(){
        AppUser appUser = new AppUser("testusername", "testpassword123","test@test.com", Role.ROLE_USER);
        entityManager.persistAndFlush(appUser);

        Profile profile = new Profile(appUser.getUsername(), "New Test Chef Profile", "image.jpg", appUser);
        entityManager.persistAndFlush(profile);

        Recipe recipe = createTestRecipe("Test recipe");
        entityManager.persistAndFlush(recipe);

        Review review1 = new Review(profile, recipe, "test comment", 5);
        entityManager.persistAndFlush(review1);

        Review review2 = new Review(profile, recipe, "test comment2", 1);
        entityManager.persistAndFlush(review2);

        double avg = reviewRepo.findAverageRatingByRecipeId(recipe.getId());

        assertEquals(3.0, avg, 0.01);
    }

    @Test
    void findAverageRatingByRecipeIdReturnsZeroWhenNoReviews(){
        AppUser appUser = new AppUser("testusername", "testpassword123","test@test.com", Role.ROLE_USER);
        entityManager.persistAndFlush(appUser);

        Profile profile = new Profile(appUser.getUsername(), "New Test Chef Profile", "image.jpg", appUser);
        entityManager.persistAndFlush(profile);

        Recipe recipe = createTestRecipe("Test recipe");
        entityManager.persistAndFlush(recipe);

        double avg = reviewRepo.findAverageRatingByRecipeId(recipe.getId());

        assertEquals(0.0, avg);
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
