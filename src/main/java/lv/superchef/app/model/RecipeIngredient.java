package lv.superchef.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.superchef.app.enums.IngredientUnit;

@Entity
@Table(name = "recipe_ingredient")
@NoArgsConstructor
@Setter
@Getter
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "recipeIngredientId")
    private Long id;

    @NotBlank
    @Column(name = "ingredient_name", nullable = false)
    private String ingredientName;

    @Column(nullable = false)
    @DecimalMin(value = "0.01", message = "Quantity must be at least 0.01")
    private double quantity;

    @NotNull
    @Column(name = "unit")
    @Enumerated(EnumType.STRING)
    private IngredientUnit unit;

}
