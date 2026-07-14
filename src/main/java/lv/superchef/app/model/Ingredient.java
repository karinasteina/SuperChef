package lv.superchef.app.model;

import jakarta.persistence.*;
import lombok.*;
import lv.superchef.app.dto.IngredientUnit;

@Entity
@Table(name = "Ingredient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;


    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Amount", nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "Unit", nullable = false)
    private IngredientUnit unit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    

}
