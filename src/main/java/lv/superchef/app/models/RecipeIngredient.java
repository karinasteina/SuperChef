package lv.superchef.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.superchef.app.enums.Units;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient
{
    private Long id;
    private String ingredientName;
    private int quantity;
    private Units unit;
}
