package lv.superchef.app.dtos;


import lombok.Data;

import java.util.List;

@Data
public class RecipeCreateDTO
{
    private String title;
    private String description;
    private String imageUrl;
    private int calories;
    private int preparationTime;
    private int cookingTime;
    private String difficulty;
    private String category;

    private List<IngredientInputDTO> ingredients;
    private List<String> steps;
}
