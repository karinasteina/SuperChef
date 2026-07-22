package lv.superchef.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Profile")
@Setter
@Getter
@NoArgsConstructor

public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "ProfileId")
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]{5,15}$", message = "Name must be 5-15 characters and contain only letters, numbers, and underscores")
    @Column(name = "DisplayName")
    private String displayName;

    @NotBlank
    @Size(max = 300, message = "Bio must not exceed 300 characters")
    @Column(name = "Bio")
    private String bio;

    @NotBlank
    @Column(name = "ProfileImageUrl")
    private String profileImageUrl;

    @NotNull
    @OneToOne
    @JoinColumn(name = "UserId", nullable = false, unique = true)
    private AppUser appUser;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recipe> recipes = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteRecipe> favoriteRecipes = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public Profile(String displayName, String bio, String profileImageUrl, AppUser appUser) {
        setDisplayName(displayName);
        setBio(bio);
        setProfileImageUrl(profileImageUrl);
        setAppUser(appUser);
    }

}
