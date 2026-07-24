package lv.superchef.app.service.impl;

import lv.superchef.app.model.Follow;
import lv.superchef.app.model.Profile;
import lv.superchef.app.repository.IFollowRepo;
import lv.superchef.app.repository.IProfileRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowServiceImpl Unit Tests")
class FollowServiceImplTest
{

    @Mock
    private IFollowRepo followRepo;

    @Mock
    private IProfileRepo profileRepo;

    @InjectMocks
    private FollowServiceImpl followService;

    @Nested
    @DisplayName("follow() Tests")
    class FollowTests
    {
        @Test
        @DisplayName("Should successfully follow another user when profiles exist and relationship is new")
        void shouldSuccessfullyFollowUser()
        {
            Long followerId = 1L;
            Long followingId = 2L;

            Profile followerProfile = new Profile();
            Profile followedProfile = new Profile();

            when(followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(false);
            when(profileRepo.findById(followerId)).thenReturn(Optional.of(followerProfile));
            when(profileRepo.findById(followingId)).thenReturn(Optional.of(followedProfile));

            followService.follow(followerId, followingId);

            verify(followRepo, times(1)).save(any(Follow.class));
        }

        @Test
        @DisplayName("Should do nothing if user is already following")
        void shouldDoNothingWhenAlreadyFollowing()
        {
            Long followerId = 1L;
            Long followingId = 2L;

            when(followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(true);

            followService.follow(followerId, followingId);

            verify(profileRepo, never()).findById(anyLong());
            verify(followRepo, never()).save(any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when parameters are null")
        void shouldThrowExceptionWhenFollowerOrFollowingIsNull()
        {
            assertThatThrownBy(() -> followService.follow(null, 2L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Parameters are null");

            assertThatThrownBy(() -> followService.follow(1L, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Parameters are null");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when attempting to follow self")
        void shouldThrowExceptionWhenFollowingSelf()
        {
            assertThatThrownBy(() -> followService.follow(1L, 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cannot follow yourself");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when follower profile does not exist")
        void shouldThrowExceptionWhenFollowerProfileNotFound()
        {
            Long followerId = 1L;
            Long followingId = 2L;

            when(followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(false);
            when(profileRepo.findById(followerId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> followService.follow(followerId, followingId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Profile with id 1 does not exist");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when target profile to follow does not exist")
        void shouldThrowExceptionWhenFollowedProfileNotFound()
        {
            Long followerId = 1L;
            Long followingId = 2L;

            Profile followerProfile = new Profile();

            when(followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(false);
            when(profileRepo.findById(followerId)).thenReturn(Optional.of(followerProfile));
            when(profileRepo.findById(followingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> followService.follow(followerId, followingId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Profile with id 2 does not exist");
        }
    }

    @Nested
    @DisplayName("unfollow() Tests")
    class UnfollowTests
    {

        @Test
        @DisplayName("Should successfully unfollow when follow relationship exists")
        void shouldSuccessfullyUnfollowUser()
        {
            Long followerId = 1L;
            Long followingId = 2L;

            when(followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(true);

            followService.unfollow(followerId, followingId);

            verify(followRepo, times(1)).deleteByFollowerIdAndFollowingId(followerId, followingId);
        }

        @Test
        @DisplayName("Should do nothing when follow relationship does not exist")
        void shouldDoNothingWhenNotFollowing()
        {
            Long followerId = 1L;
            Long followingId = 2L;

            when(followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(false);

            followService.unfollow(followerId, followingId);

            verify(followRepo, never()).deleteByFollowerIdAndFollowingId(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when parameters are null")
        void shouldThrowExceptionWhenUnfollowParamsAreNull()
        {
            assertThatThrownBy(() -> followService.unfollow(null, 2L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Parameters are null");

            assertThatThrownBy(() -> followService.unfollow(1L, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Parameters are null");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when attempting to unfollow self")
        void shouldThrowExceptionWhenUnfollowingSelf()
        {
            assertThatThrownBy(() -> followService.unfollow(1L, 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cannot unfollow yourself");
        }
    }
}