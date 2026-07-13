package lv.superchef.app.repository;

import lv.superchef.app.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAppUserRepo extends JpaRepository<AppUser, Long> {
    boolean existsByUsername(String username);


    boolean existsByEmail(String email);
}
