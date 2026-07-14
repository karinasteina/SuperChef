package lv.superchef.app.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "FavoriteRecipe")
public class FavoriteRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "FavoriteRecipeId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "AppUserId")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "RecipeId")
    private Recipe recipe;


}
