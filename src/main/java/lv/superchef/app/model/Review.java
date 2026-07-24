package lv.superchef.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "review", uniqueConstraints = {@UniqueConstraint(name = "uk_review_profile_recipe", columnNames = {"profile_id", "recipe_id"})})
@Getter
@Setter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name = "review_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @NotBlank(message = "The comment cannot be blank")
    @Size(max = 2000, message = "Review length cannot exceed 2000 symbols")
    @Column(length = 2000)
    private String comment;

    @NotNull
    @Min(value = 1, message = "Review rating must be at least 1")
    @Max(value = 5, message = "Review rating cannot be greater than 5")
    @Column(name = "rating")
    private int rating;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // ?

    public Review(Profile profile, Recipe recipe, String comment, Integer rating) {
        this.profile = profile;
        this.recipe = recipe;
        this.comment = comment;
        this.rating = rating;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}