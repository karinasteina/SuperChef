package lv.superchef.app;

import lv.superchef.app.enums.Role;
import lv.superchef.app.model.AppUser;
import lv.superchef.app.model.Profile;
import lv.superchef.app.repository.IAppUserRepo;
import lv.superchef.app.repository.IProfileRepo;
import lv.superchef.app.repository.IRecipeRepo;
import lv.superchef.app.service.IRecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppApplication CommandLineRunner Unit Tests")
class AppApplicationTest {

	@Mock
	private IAppUserRepo userRepo;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private IRecipeService recipeService;

	@Mock
	private IProfileRepo profileRepo;

	@Mock
	private IRecipeRepo recipeRepo;

	private AppApplication application;

	@BeforeEach
	void setUp()
	{
		application = new AppApplication();
		lenient().when(passwordEncoder.encode(any())).thenAnswer(invocation -> "encoded_" + invocation.getArgument(0));

		AtomicLong idGenerator = new AtomicLong(1L);
		lenient().when(userRepo.save(any(AppUser.class))).thenAnswer(invocation -> {
			AppUser user = invocation.getArgument(0);
			if (user.getId() == null)
			{
				ReflectionTestUtils.setField(user, "id", idGenerator.getAndIncrement());
			}
			return user;
		});
	}

	@Test
	@DisplayName("Should seed admin and user when they do not exist")
	void shouldSeedUsersAndProfilesWhenMissing() throws Exception
	{
		when(userRepo.findByUsername("testadmin")).thenReturn(null);
		when(userRepo.findByUsername("testuser")).thenReturn(null);
		when(profileRepo.findByAppUser_Id(anyLong())).thenReturn(Optional.empty());

		CommandLineRunner runner = application.testDB(userRepo, passwordEncoder, recipeService, profileRepo, recipeRepo);
		runner.run();

		verify(userRepo, atLeastOnce()).findByUsername("testadmin");
		verify(userRepo, atLeastOnce()).findByUsername("testuser");
		verify(userRepo, atLeast(2)).save(any(AppUser.class));
	}

	@Test
	@DisplayName("Should skip creating testadmin and testuser when they already exist")
	void shouldNotDuplicateUsersOrProfilesWhenAlreadyExist() throws Exception
	{
		AppUser existingAdmin = new AppUser("testadmin", "pass", "admin@test.lv", Role.ROLE_ADMIN);
		ReflectionTestUtils.setField(existingAdmin, "id", 1L);

		AppUser existingUser = new AppUser("testuser", "pass", "user@test.lv", Role.ROLE_USER);
		ReflectionTestUtils.setField(existingUser, "id", 2L);

		when(userRepo.findByUsername("testadmin")).thenReturn(existingAdmin);
		when(userRepo.findByUsername("testuser")).thenReturn(existingUser);

		when(userRepo.findByUsername(argThat(username -> !"testadmin".equals(username) && !"testuser".equals(username))))
				.thenAnswer(invocation -> {
					String username = invocation.getArgument(0);
					AppUser demoUser = new AppUser(username, "pass", username + "@test.lv", Role.ROLE_USER);
					ReflectionTestUtils.setField(demoUser, "id", 99L);
					return demoUser;
				});

		when(profileRepo.findByAppUser_Id(anyLong())).thenReturn(Optional.of(new Profile()));

		CommandLineRunner runner = application.testDB(userRepo, passwordEncoder, recipeService, profileRepo, recipeRepo);
		runner.run();

		verify(userRepo).findByUsername("testadmin");
		verify(userRepo).findByUsername("testuser");

		verify(userRepo, never()).save(argThat(user ->
				"testadmin".equals(user.getUsername()) || "testuser".equals(user.getUsername())
		));
	}
}