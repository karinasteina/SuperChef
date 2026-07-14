package lv.superchef.app.repository;

import lv.superchef.app.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProfileRepo extends JpaRepository<Profile, Long> {
    Optional<Profile> findByAppUser_Id(Long userId);
}
