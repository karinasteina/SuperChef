package lv.superchef.app.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "recipes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe
{
//id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
//title
    @NotBlank(message = "Recipe title cannot be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false, length = 100)
    private String title;
//description
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
//imageurl
    @NotBlank(message = "Image URL is required")
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
//calories
    @PositiveOrZero(message = "Calories cannot be negative")
    @Column(nullable = false)
    private int calories;
//prep time
    @Min(value = 1, message = "Preparation time must be at least 1 minute, cannot be negative")
    @Column(name = "preparation_time", nullable = false)
    private int preparationTime;
//cooking time
    @Min(value = 0, message = "Cooking time must be at least 0 minute, cannot be negative")
    @Column(name = "cooking_time",nullable = false)
    private int cookingTime;
//difficulty
    @NotBlank(message = "Difficulty level is required")
    @Column(nullable = false)
    private String difficulty;
//category
    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;
//ingredients
    @NotEmpty(message = "Ingredients list cannot be empty")
    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<RecipeIngredient> ingredients = new ArrayList<>();
//steps
    @NotEmpty(message = "Steps list cannot be empty")
    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<RecipeStep> steps = new ArrayList<>();
//created at
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
//updated at
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
