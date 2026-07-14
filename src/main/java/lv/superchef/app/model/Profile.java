package lv.superchef.app.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "ProfileId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private AppUser appUser;
    //
    //    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    //    private List<FavoriteRecipe> favoriteRecipes = new ArrayList<>();

    //DO WE KEEP THIS CLASS???
}
