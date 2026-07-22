package lv.superchef.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;

@Getter
@Setter
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private int calories;
    private int preparationTime;
    private int cookingTime;
    private String difficulty;
    private String category;
    private ProfileDTO author;

    public static RecipeDTO mapToDto(Recipe recipe){
        return new RecipeDTO(recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getImageUrl(),
                recipe.getCalories(),
                recipe.getPreparationTime(),
                recipe.getCookingTime(),
                recipe.getDifficulty(),
                recipe.getCategory(),
                ProfileDTO.mapToDto(recipe.getAuthor()));
    }
}
