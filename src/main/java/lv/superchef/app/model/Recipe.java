package lv.superchef.app.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "Recipe")
@Data
@NoArgsConstructor
public class Recipe {

    // add comments to the column annotation

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "recipeId", comment = "Primary key identifier for the recipe")
    private Long id;

    @NotBlank(message = "Recipe title cannot be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false, length = 100, comment = "Title of the recipe")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(nullable = false, columnDefinition = "TEXT", comment = "Description of the recipe")
    private String description;

    @NotBlank(message = "Image URL is required")
    @Column(name = "image_url", nullable = false, comment = "URL pointing to the recipe cover image")
    private String imageUrl;

    @PositiveOrZero(message = "Calories cannot be negative")
    @Column(name = "calories", comment = "Estimated calorie count per serving") // will have default vals in db if not set
    private int calories;

    @Min(value = 1, message = "Preparation time must be at least 1 minute, cannot be negative")
    @Column(name = "preparation_time", nullable = false)
    private int preparationTime;

    @Min(value = 0, message = "Cooking time must be at least 0 minute, cannot be negative")
    @Column(name = "cooking_time", nullable = false, comment = "Preparation time in minutes")
    private int cookingTime;

    @NotBlank(message = "Difficulty level is required")
    @Column(nullable = false, comment = "Skill level required")
    private String difficulty;

    @NotBlank(message = "Category is required")
    @Column(nullable = false, comment = "Category is required", name = "")
    private String category;

    @NotEmpty(message = "Ingredients list cannot be empty")
    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @NotEmpty(message = "Steps list cannot be empty")
    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<RecipeStep> steps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    // lazy loading where we can

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private Profile author;
}
