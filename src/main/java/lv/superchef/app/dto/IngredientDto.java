package lv.superchef.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.superchef.app.enums.IngredientUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDto {

    private String name;
    private double amount;
    private IngredientUnit unit;

}

