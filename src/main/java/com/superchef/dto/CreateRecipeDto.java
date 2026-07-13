package com.superchef.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRecipeDto {

    private String title;
    private String description;
    private Integer calories;
    private Integer preparationTime;
    private Integer cookingTime;
    private String difficulty;
    private String category;

    @Builder.Default
    private List<IngredientDto> ingredients = new ArrayList<>(List.of(new IngredientDto("", "")));

    @Builder.Default
    private List<String> steps = new ArrayList<>(List.of(""));

}

