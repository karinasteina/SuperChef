package lv.superchef.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "recipe_step")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStep {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "recipeStepId")
    private Long id;

    @Min(value = 1, message = "Step number must be at least 1")
    @Column(name = "step_number")
    private int stepNumber;

    @NotBlank(message = "Step instruction cannot be blank")
    @Size(max = 1000, message = "Step instruction cannot exceed 1000 characters")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String instruction;
}
