package lv.superchef.app.repo;

import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.enums.Role;
import lv.superchef.app.model.*;
import lv.superchef.app.repository.IRecipeRepo;
import lv.superchef.app.repository.RecipeSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Recipe Specifications Repository Unit Tests")
@ActiveProfiles("test")
class RecipeSpecificationsTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IRecipeRepo recipeRepo;

    private Recipe quickPasta;
    private Recipe slowSteak;

    @BeforeEach
    void setUp()
    {
        AppUser testUser = new AppUser(
                "raivis",
                "superSecurePassword123",
                "raivis@example.com",
                Role.ROLE_USER
        );
        entityManager.persist(testUser);

        Profile testProfile = new Profile(
                testUser.getUsername(),
                "Test racoon",
                "/images/racoon.jpg",
                testUser
        );
        entityManager.persist(testProfile);

        RecipeIngredient pastaIngredient = new RecipeIngredient();
        pastaIngredient.setIngredientName("Garlic");
        pastaIngredient.setQuantity(2.0);
        pastaIngredient.setUnit(IngredientUnit.PIECE);

        RecipeStep pastaStep = new RecipeStep();
        pastaStep.setStepNumber(1);
        pastaStep.setInstruction("Mince garlic and boil pasta.");

        quickPasta = new Recipe();
        quickPasta.setTitle("Creamy Garlic Pasta");
        quickPasta.setDescription("Delicious Italian dinner in 10 minutes");
        quickPasta.setImageUrl("https://example.com/pasta.jpg");
        quickPasta.setCategory("Dinner");
        quickPasta.setDifficulty("Easy");
        quickPasta.setCalories(450);
        quickPasta.setPreparationTime(5);
        quickPasta.setCookingTime(10);
        quickPasta.setAuthor(testProfile);
        quickPasta.getIngredients().add(pastaIngredient);
        quickPasta.getSteps().add(pastaStep);

        RecipeIngredient steakIngredient = new RecipeIngredient();
        steakIngredient.setIngredientName("Ribeye Steak");
        steakIngredient.setQuantity(300.0);
        steakIngredient.setUnit(IngredientUnit.G);

        RecipeStep steakStep = new RecipeStep();
        steakStep.setStepNumber(1);
        steakStep.setInstruction("Season steak liberally with salt.");

        slowSteak = new Recipe();
        slowSteak.setTitle("Grilled Ribeye Steak");
        slowSteak.setDescription("Juicy steak with herbs");
        slowSteak.setImageUrl("https://example.com/steak.jpg");
        slowSteak.setCategory("Dinner");
        slowSteak.setDifficulty("Hard");
        slowSteak.setCalories(850);
        slowSteak.setPreparationTime(15);
        slowSteak.setCookingTime(30);
        slowSteak.setAuthor(testProfile);
        slowSteak.getIngredients().add(steakIngredient);
        slowSteak.getSteps().add(steakStep);

        entityManager.persist(quickPasta);
        entityManager.persist(slowSteak);
        entityManager.flush();
    }

    @Nested
    @DisplayName("Keyword Filter Tests")
    class KeywordFilterTests
    {

        @Test
        @DisplayName("Should match recipe title case-insensitively")
        void shouldMatchKeywordInTitle()
        {
            Specification<Recipe> spec = RecipeSpecifications.recipeByFilter("pasta", null, null, null, null, null);
            List<Recipe> results = recipeRepo.findAll(spec);

            assertThat(results).containsExactly(quickPasta);
        }

        @Test
        @DisplayName("Should match recipe description case-insensitively")
        void shouldMatchKeywordInDescription()
        {
            Specification<Recipe> spec = RecipeSpecifications.recipeByFilter("juicy", null, null, null, null, null);
            List<Recipe> results = recipeRepo.findAll(spec);

            assertThat(results).containsExactly(slowSteak);
        }

        @Test
        @DisplayName("Should return all recipes when keyword is blank or null")
        void shouldReturnAllWhenKeywordIsBlank()
        {
            Specification<Recipe> spec = RecipeSpecifications.recipeByFilter("   ", null, null, null, null, null);
            List<Recipe> results = recipeRepo.findAll(spec);

            assertThat(results).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Category & Difficulty Filter Tests")
    class CategoryAndDifficultyTests
    {

        @Test
        @DisplayName("Should filter correctly by difficulty")
        void shouldFilterByDifficulty()
        {
            Specification<Recipe> spec = RecipeSpecifications.recipeByFilter(null, null, "Easy", null, null, null);
            List<Recipe> results = recipeRepo.findAll(spec);

            assertThat(results).containsExactly(quickPasta);
        }

        @Test
        @DisplayName("Should filter correctly by category")
        void shouldFilterByCategory()
        {
            Specification<Recipe> spec = RecipeSpecifications.recipeByFilter(null, "Dinner", null, null, null, null);
            List<Recipe> results = recipeRepo.findAll(spec);

            assertThat(results).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Numeric Ranges Filter Tests")
    class NumericFilterTests
    {

        @Test
        @DisplayName("Should filter by max calories")
        void shouldFilterByMaxCalories()
        {
            Specification<Recipe> spec = RecipeSpecifications.recipeByFilter(null, null, null, 500, null, null);
            List<Recipe> results = recipeRepo.findAll(spec);

            assertThat(results).containsExactly(quickPasta);
        }

        @Test
        @DisplayName("Should filter by max prep time")
        void shouldFilterByMaxPrepTime()
        {
            Specification<Recipe> spec = RecipeSpecifications.recipeByFilter(null, null, null, null, 10, null);
            List<Recipe> results = recipeRepo.findAll(spec);

            assertThat(results).containsExactly(quickPasta);
        }

        @Test
        @DisplayName("Should filter by max cooking time")
        void shouldFilterByMaxCookTime()
        {
            Specification<Recipe> spec = RecipeSpecifications.recipeByFilter(null, null, null, null, null, 20);
            List<Recipe> results = recipeRepo.findAll(spec);

            assertThat(results).containsExactly(quickPasta);
        }
    }

    @Nested
    @DisplayName("Combined Filters Test")
    class CombinedFilterTests
    {

        @Test
        @DisplayName("Should filter matching all applied criteria")
        void shouldFilterByMultipleCriteria()
        {
            Specification<Recipe> spec = RecipeSpecifications.recipeByFilter(
                    "garlic", "Dinner", "Easy", 500, 10, 15
            );
            List<Recipe> results = recipeRepo.findAll(spec);

            assertThat(results).containsExactly(quickPasta);
        }

        @Test
        @DisplayName("Should return empty list when criteria conflict")
        void shouldReturnEmptyWhenNoMatch()
        {
            Specification<Recipe> spec = RecipeSpecifications.recipeByFilter(
                    "garlic", "Dinner", "Hard", 500, null, null
            );
            List<Recipe> results = recipeRepo.findAll(spec);

            assertThat(results).isEmpty();
        }
    }
}