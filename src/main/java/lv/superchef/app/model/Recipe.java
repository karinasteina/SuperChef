package lv.superchef.app.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "Recipe")
@Data
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "recipeId")
    private Long id;

    @NotBlank(message = "Recipe title cannot be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Image URL is required")
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @PositiveOrZero(message = "Calories cannot be negative")
    @Column(name = "calories")
    private int calories;

    @Min(value = 1, message = "Preparation time must be at least 1 minute, cannot be negative")
    @Column(name = "preparation_time", nullable = false)
    private int preparationTime;

    @Min(value = 0, message = "Cooking time must be at least 0 minute, cannot be negative")
    @Column(name = "cooking_time", nullable = false)
    private int cookingTime;

    @NotBlank(message = "Difficulty level is required")
    @Column(nullable = false)
    private String difficulty;

    @NotBlank(message = "Category is required")
    @Column(name = "category", nullable = false)
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private Profile author;
}
