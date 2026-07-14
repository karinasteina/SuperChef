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
    @JoinColumn(name = "ProfileId")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "RecipeId")
    private Recipe recipe;

    public FavoriteRecipe(Recipe recipe, Profile profile) {
        this.recipe = recipe;
        this.profile = profile;
    }

    public FavoriteRecipe() {
    }

}
