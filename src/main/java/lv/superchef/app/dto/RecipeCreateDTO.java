package lv.superchef.app.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RecipeCreateDTO
{
    @NotBlank(message = "Recipe title cannot be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private String imageUrl;

    @PositiveOrZero(message = "Calories cannot be negative")
    private int calories;

    @Min(value = 1, message = "Preparation time must be at least 1 minute, cannot be negative")
    private int preparationTime;

    @Min(value = 0, message = "Cooking time must be at least 0 minute, cannot be negative")
    private int cookingTime;

    @NotBlank(message = "Difficulty level is required")
    private String difficulty;

    @NotBlank(message = "Category is required")
    private String category;

    @NotEmpty(message = "A recipe must have at least one ingredient")
    @Valid
    private List<IngredientInputDTO> ingredients;

    @NotEmpty(message = "A recipe must have at least one instruction step")
    private List<@NotBlank(message = "Step instruction cannot be empty") String> steps;
}
