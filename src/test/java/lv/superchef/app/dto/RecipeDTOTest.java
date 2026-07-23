package lv.superchef.app.dto;

import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RecipeDTO Unit Tests")
class RecipeDTOTest {

    @Test
    @DisplayName("Should correctly map Recipe entity and nested Profile to RecipeDTO")
    void shouldMapRecipeToRecipeDto()
    {
        Profile author = new Profile();
        author.setDisplayName("Chef Raivis");
        author.setBio("Passionate about Italian cuisine");

        Recipe recipe = new Recipe();
        recipe.setTitle("Spaghetti Carbonara");
        recipe.setDescription("Classic pasta dish with pork and egg.");
        recipe.setImageUrl("https://example.com/carbonara.jpg");
        recipe.setCalories(650);
        recipe.setPreparationTime(15);
        recipe.getCookingTime();
        recipe.setCookingTime(20);
        recipe.setDifficulty("Medium");
        recipe.setCategory("Pasta");
        recipe.setAuthor(author);

        RecipeDTO dto = RecipeDTO.mapToDto(recipe);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(recipe.getId());
        assertThat(dto.getTitle()).isEqualTo("Spaghetti Carbonara");
        assertThat(dto.getDescription()).isEqualTo("Classic pasta dish with pork and egg.");
        assertThat(dto.getImageUrl()).isEqualTo("https://example.com/carbonara.jpg");
        assertThat(dto.getCalories()).isEqualTo(650);
        assertThat(dto.getPreparationTime()).isEqualTo(15);
        assertThat(dto.getCookingTime()).isEqualTo(20);
        assertThat(dto.getDifficulty()).isEqualTo("Medium");
        assertThat(dto.getCategory()).isEqualTo("Pasta");

        // Verify nested ProfileDTO mapping
        assertThat(dto.getAuthor()).isNotNull();
        assertThat(dto.getAuthor().getDisplayName()).isEqualTo("Chef Raivis");
        assertThat(dto.getAuthor().getBio()).isEqualTo("Passionate about Italian cuisine");
    }

    @Test
    @DisplayName("Should throw NullPointerException when recipe author is null")
    void shouldThrowExceptionWhenAuthorIsNull()
    {
        Recipe recipe = new Recipe();
        recipe.setTitle("Quick Salad");
        recipe.setAuthor(null);

        assertThatThrownBy(() -> RecipeDTO.mapToDto(recipe))
                .isInstanceOf(NullPointerException.class);
    }
}