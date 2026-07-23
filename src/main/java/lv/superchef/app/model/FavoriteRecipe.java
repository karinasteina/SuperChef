package lv.superchef.app.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "favorite_recipe", uniqueConstraints = {@UniqueConstraint(name = "uk_favorite_profile_recipe", columnNames = {"profile_id", "recipe_id"})})
public class FavoriteRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "favorite_recipe_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    protected FavoriteRecipe() {
    }

    public FavoriteRecipe(Recipe recipe, Profile profile) {
        this.recipe = recipe;
        this.profile = profile;
    }
}