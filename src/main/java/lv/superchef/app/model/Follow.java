package lv.superchef.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="Follow")
@Getter
@Setter
@NoArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name="FollowId")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name="followerId", nullable = false)
    private Profile follower;

    @NotNull
    @ManyToOne
    @JoinColumn(name="followingId", nullable = false)
    private Profile following;

    public Follow(Profile follower, Profile following){
        setFollower(follower);
        setFollowing(following);
    }
}
