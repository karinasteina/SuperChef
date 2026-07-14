package lv.superchef.app.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.superchef.app.enums.Units;

@Entity
@Table(name = "recipe_ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ingredient_name",nullable = false)
    private String ingredientName;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private Units unit;
}
