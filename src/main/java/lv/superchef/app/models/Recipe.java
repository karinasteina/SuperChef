package lv.superchef.app.models;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe
{
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private int calories;
    private int preparationTime;
    private int cookingTime;
    private String difficulty;
    private String category;

    private List<RecipeIngredient> ingredients = new ArrayList<>();
    private List<RecipeStep> steps = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
