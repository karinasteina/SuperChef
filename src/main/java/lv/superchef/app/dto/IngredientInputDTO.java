package lv.superchef.app.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.superchef.app.enums.IngredientUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientInputDTO
{
    @NotBlank(message = "Ingredient name cannot be blank")
    @Size(min = 2, max = 100, message = "Ingredient name must be between 2 and 100 characters")
    private String name;

    @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
    private double quantity;

    @NotBlank(message = "Unit of measurement is required")
    private IngredientUnit unit;
}
