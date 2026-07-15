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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "UserProfile")
@Setter
@Getter
@NoArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name="ProfileId")
    private Long id;

    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9_]{5,15}$",
            message = "Name must be 5-15 characters and contain only letters, numbers, and underscores"
    )
    @Column(name="DisplayName")
    private String displayName;

    @NotBlank
    @Size(max = 300, message = "Bio must not exceed 300 characters")
    @Column(name="Bio")
    private String bio;

    @NotBlank
    @Pattern(regexp = "^(?:(?:https?|ftp|file)://\\S+|/\\S+)$", message = "Must be a valid URL format")
    @Column(name="ProfileImageUrl")
    private String profileImageUrl;

    @NotNull
    @OneToOne
    @JoinColumn(name = "AppUserId", referencedColumnName = "UserId", nullable = false, unique = true)
    private AppUser appUser;

    @NotNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ProfileId")
    private List<Recipe> recipes = new ArrayList<>();

    @NotNull
    @ManyToMany(mappedBy = "followedProfiles")
    private Set<Profile> followers = new HashSet<>();

    @NotNull
    @ManyToMany
    @JoinTable(
            name = "ProfileFollows",
            joinColumns = @JoinColumn(name="FollowerId"),
            inverseJoinColumns = @JoinColumn(name="FollowedId")
    )
    private Set<Profile> followedProfiles = new HashSet<>();

    @NotNull
    @ManyToMany
    @JoinTable(name="SavedRecipes")
    private Set<Recipe> savedRecipes = new HashSet<>();

    public Profile(String displayName, String bio, String profileImageUrl, AppUser appUser){
        setDisplayName(displayName);
        setBio(bio);
        setProfileImageUrl(profileImageUrl);
        setAppUser(appUser);
    }

    public void saveRecipe(Recipe recipe){
        savedRecipes.add(recipe);
    }

    public void removeRecipe(Recipe recipe){
        savedRecipes.remove(recipe);
    }

    public void followProfile(Profile profile){
        followedProfiles.add(profile);
        profile.addFollower(this);
    }

    public void addFollower(Profile profile){
        followers.add(profile);
    }

    public void removeFollower(Profile profile){
        followers.remove(profile);
    }

    public void unfollowProfile(Profile profile){
        followedProfiles.remove(profile);
        profile.removeFollower(this);
    }
}
