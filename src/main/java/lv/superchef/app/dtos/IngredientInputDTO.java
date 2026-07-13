package lv.superchef.app.dtos;

import lombok.Data;
import lv.superchef.app.enums.Units;

@Data
public class IngredientInputDTO
{
    private String name;
    private int quantity;
    private Units unit;
}
