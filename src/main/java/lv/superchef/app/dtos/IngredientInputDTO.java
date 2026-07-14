package lv.superchef.app.dtos;

import lombok.Data;
import lv.superchef.app.enums.IngredientUnit;

@Data
public class IngredientInputDTO
{
    private String name;
    private double quantity;
    private IngredientUnit unit;
}
