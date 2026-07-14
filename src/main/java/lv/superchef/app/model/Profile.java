package lv.superchef.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashSet;

@Entity
@Table(name = "UserProfile")
public class Profile {
    private Long id;
    private String displayName;
    private String bio;
    private String profileImageUrl;
    private AppUser appUser;
    private List<Recipe> recipes = new ArrayList<>();
    private Set<Profile> followers = new HashSet<>();
    private Set<Recipe> savedRecipes = new HashSet<>();
}
