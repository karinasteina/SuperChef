package lv.superchef.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.superchef.app.enums.IngredientUnit;

@Data
@Entity
@Table(name="recipe_ingredients")
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ingredientName;
    private double quantity;
    private IngredientUnit unit;
}