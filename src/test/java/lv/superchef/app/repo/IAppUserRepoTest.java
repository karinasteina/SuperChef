package lv.superchef.app.repo;

import lv.superchef.app.enums.Role;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.repository.IAppUserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class IAppUserRepoTest {
    @Autowired
    IAppUserRepo userRepo;

    @Autowired
    TestEntityManager entityManager;


    @Test
    void givenExistingUsernameDBReturnsTrue(){
        AppUser appUser = createTestUser();

        entityManager.persistAndFlush(appUser);

        assertTrue(userRepo.existsByUsername("userforatest"));
    }

    @Test
    void givenNonExistingUsernameDBReturnsFalse(){
        assertFalse(userRepo.existsByUsername("usernamedoesntexist"));
    }

    @Test
    void givenExistingEmailDBReturnsTrue(){
        AppUser appUser = createTestUser();
        entityManager.persistAndFlush(appUser);

        assertTrue(userRepo.existsByEmail("user@user.com"));
    }

    @Test
    void givenNonExistingEmailDBReturnsFalse(){
        assertFalse(userRepo.existsByEmail("usernonexisting@user.com"));
    }

    @Test
    void givenExistingUsernameFindByUsernameReturnsUser(){
        AppUser appUser = createTestUser();
        entityManager.persistAndFlush(appUser);

        AppUser result = userRepo.findByUsername("userforatest");
        assertNotNull(result);
        assertEquals(appUser.getId(), result.getId());
        assertEquals("userforatest", result.getUsername());

    }

    @Test
    void givenNonExistingUsernameFindByUsernameReturnsNull(){
        AppUser result = userRepo.findByUsername("doesNotExist");

        assertNull(result);

    }


    private AppUser createTestUser(){
        AppUser appUser = new AppUser();
        appUser.setUsername("userforatest");
        appUser.setPassword("123userforatest123");
        appUser.setEmail("user@user.com");
        appUser.setRole(Role.ROLE_USER);

        return appUser;
    }

}
