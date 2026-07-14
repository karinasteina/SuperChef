package lv.superchef.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lv.superchef.app.enums.IngredientUnit;

@Entity
@Table(name = "recipe_ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient
{
    //id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    //name
    @Column(name = "ingredient_name",nullable = false)
    private String ingredientName;
    //quantity
    @Column(nullable = false)
    @DecimalMin(value = "0.01", message = "Quantity must be at least 0.01")
    private double quantity;
    //unit
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IngredientUnit unit;
}
