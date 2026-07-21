package lv.superchef.app.repository;

import lv.superchef.app.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFollowRepo extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
