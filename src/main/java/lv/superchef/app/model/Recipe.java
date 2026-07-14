package lv.superchef.app.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "RecipeId")
    private Long id;

    @Column(name = "Title")
    @NotBlank
    private String title;

    @Column(name = "Description")
    @NotBlank
    private String description;

    @Column(name = "Calories")
    @NotNull
    @PositiveOrZero
    private Integer calories;

    @Column(name = "PreparationTime")
    @NotNull
    @PositiveOrZero
    private Integer preparationTime;

    @Column(name = "CookingTime")
    @NotNull
    @PositiveOrZero
    private Integer cookingTime;

    @Column(name = "Difficulty")
    @NotBlank
    private String difficulty;

    @Column(name = "Category")
    @NotBlank
    private String category;


    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredients = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "Step", joinColumns = @JoinColumn(name = "RecipeId"))
    @OrderColumn(name = "step_position")
    private List<String> steps = new ArrayList<>();

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }

    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.setRecipe(null);
    }


}
