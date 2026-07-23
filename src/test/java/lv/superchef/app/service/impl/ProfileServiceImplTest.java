package lv.superchef.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lv.superchef.app.model.Follow;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.repository.IFollowRepo;
import lv.superchef.app.repository.IProfileRepo;
import lv.superchef.app.repository.IRecipeRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileServiceImpl Unit Tests")
class ProfileServiceImplTest {

    @Mock
    private IProfileRepo profileRepo;

    @Mock
    private IFollowRepo followRepo;

    @Mock
    private IRecipeRepo recipeRepo;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Nested
    @DisplayName("getProfileByUserId Tests")
    class GetProfileByUserIdTests
    {

        @Test
        @DisplayName("Should return Optional containing Profile when found by userId")
        void shouldReturnProfileWhenUserIdExists()
        {
            Long userId = 1L;
            Profile expectedProfile = new Profile();
            when(profileRepo.findByAppUser_Id(userId)).thenReturn(Optional.of(expectedProfile));

            Optional<Profile> result = profileService.getProfileByUserId(userId);

            assertThat(result).isPresent().contains(expectedProfile);
            verify(profileRepo, times(1)).findByAppUser_Id(userId);
        }

        @Test
        @DisplayName("Should return empty Optional when profile not found by userId")
        void shouldReturnEmptyWhenUserIdNotFound()
        {
            Long userId = 99L;
            when(profileRepo.findByAppUser_Id(userId)).thenReturn(Optional.empty());

            Optional<Profile> result = profileService.getProfileByUserId(userId);

            assertThat(result).isEmpty();
            verify(profileRepo, times(1)).findByAppUser_Id(userId);
        }
    }

    @Nested
    @DisplayName("getAllProfiles Tests")
    class GetAllProfilesTests
    {

        @Test
        @DisplayName("Should return list of all profiles")
        void shouldReturnAllProfiles()
        {
            List<Profile> expectedProfiles = List.of(new Profile(), new Profile());
            when(profileRepo.findAll()).thenReturn(expectedProfiles);

            List<Profile> result = profileService.getAllProfiles();

            assertThat(result).hasSize(2).isEqualTo(expectedProfiles);
            verify(profileRepo, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getProfileById Tests")
    class GetProfileByIdTests
    {

        @Test
        @DisplayName("Should return Profile when valid ID exists")
        void shouldReturnProfileWhenValidIdExists()
        {
            Long profileId = 1L;
            Profile expectedProfile = new Profile();
            when(profileRepo.findById(profileId)).thenReturn(Optional.of(expectedProfile));

            Profile result = profileService.getProfileById(profileId);

            assertThat(result).isEqualTo(expectedProfile);
            verify(profileRepo, times(1)).findById(profileId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when profile ID does not exist")
        void shouldThrowExceptionWhenProfileNotFound()
        {
            Long profileId = 99L;
            when(profileRepo.findById(profileId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> profileService.getProfileById(profileId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Profile with id 99 not found");

            verify(profileRepo, times(1)).findById(profileId);
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -100L})
        @DisplayName("Should throw IllegalArgumentException when profile ID is invalid (<= 0)")
        void shouldThrowExceptionWhenIdIsInvalid(Long invalidId)
        {
            assertThatThrownBy(() -> profileService.getProfileById(invalidId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Incorrect params");

            verifyNoInteractions(profileRepo);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when profile ID is null")
        void shouldThrowExceptionWhenIdIsNull()
        {
            assertThatThrownBy(() -> profileService.getProfileById(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Incorrect params");

            verifyNoInteractions(profileRepo);
        }
    }

    @Nested
    @DisplayName("getRecipesByProfileId Tests")
    class GetRecipesByProfileIdTests
    {

        @Test
        @DisplayName("Should return recipes for a valid profile ID")
        void shouldReturnRecipesForValidProfileId()
        {
            Long profileId = 1L;
            List<Recipe> expectedRecipes = List.of(new Recipe(), new Recipe());
            when(recipeRepo.findByAuthor_Id(profileId)).thenReturn(expectedRecipes);

            List<Recipe> result = profileService.getRecipesByProfileId(profileId);

            assertThat(result).hasSize(2).isEqualTo(expectedRecipes);
            verify(recipeRepo, times(1)).findByAuthor_Id(profileId);
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L})
        @DisplayName("Should throw IllegalArgumentException when profile ID is <= 0")
        void shouldThrowExceptionWhenProfileIdIsInvalid(Long invalidId)
        {
            assertThatThrownBy(() -> profileService.getRecipesByProfileId(invalidId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Incorrect params");

            verifyNoInteractions(recipeRepo);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when profile ID is null")
        void shouldThrowExceptionWhenProfileIdIsNull()
        {
            assertThatThrownBy(() -> profileService.getRecipesByProfileId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Incorrect params");

            verifyNoInteractions(recipeRepo);
        }
    }

    @Nested
    @DisplayName("getFollowedProfiles Tests")
    class GetFollowedProfilesTests
    {

        @Test
        @DisplayName("Should return list of followed profiles")
        void shouldReturnFollowedProfiles()
        {
            Long profileId = 1L;

            Profile follower = new Profile();
            Profile following1 = new Profile();
            Profile following2 = new Profile();

            Follow follow1 = new Follow(follower, following1);
            Follow follow2 = new Follow(follower, following2);

            when(followRepo.findByFollower_Id(profileId)).thenReturn(List.of(follow1, follow2));

            List<Profile> result = profileService.getFollowedProfiles(profileId);

            assertThat(result).containsExactly(following1, following2);
            verify(followRepo, times(1)).findByFollower_Id(profileId);
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -5L})
        @DisplayName("Should throw IllegalArgumentException when profile ID is <= 0")
        void shouldThrowExceptionWhenProfileIdIsInvalid(Long invalidId)
        {
            assertThatThrownBy(() -> profileService.getFollowedProfiles(invalidId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Incorrect params");

            verifyNoInteractions(followRepo);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when profile ID is null")
        void shouldThrowExceptionWhenProfileIdIsNull()
        {
            assertThatThrownBy(() -> profileService.getFollowedProfiles(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Incorrect params");

            verifyNoInteractions(followRepo);
        }
    }

    @Nested
    @DisplayName("getFollowedProfileIds Tests")
    class GetFollowedProfileIdsTests
    {

        @Test
        @DisplayName("Should return set of followed profile IDs")
        void shouldReturnFollowedProfileIds()
        {
            Long profileId = 1L;

            Profile follower = new Profile();
            Profile following1 = mock(Profile.class);
            Profile following2 = mock(Profile.class);

            when(following1.getId()).thenReturn(10L);
            when(following2.getId()).thenReturn(20L);

            Follow follow1 = new Follow(follower, following1);
            Follow follow2 = new Follow(follower, following2);

            when(followRepo.findByFollower_Id(profileId)).thenReturn(List.of(follow1, follow2));

            Set<Long> result = profileService.getFollowedProfileIds(profileId);

            assertThat(result).containsExactlyInAnyOrder(10L, 20L);
            verify(followRepo, times(1)).findByFollower_Id(profileId);
        }

        @Test
        @DisplayName("Should return empty Set when profileId is null")
        void shouldReturnEmptySetWhenProfileIdIsNull()
        {
            Set<Long> result = profileService.getFollowedProfileIds(null);

            assertThat(result).isEmpty();
            verifyNoInteractions(followRepo);
        }
    }
}