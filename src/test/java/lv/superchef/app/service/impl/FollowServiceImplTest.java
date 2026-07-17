package lv.superchef.app.service.impl;

import lv.superchef.app.dto.RegisterRequest;
import lv.superchef.app.repository.IAppUserRepo;
import lv.superchef.app.repository.IFollowRepo;
import lv.superchef.app.repository.IProfileRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("Follow Service Unit Tests")
public class FollowServiceImplTest {

    @Mock
    private IFollowRepo followRepo;

    @Mock
    private IProfileRepo profileRepo;

    @InjectMocks
    private FollowServiceImpl followService;

   @Test
   @DisplayName("Follow fails when parameters are null")
   public void followFailsWhenParametersAreNull(){
       IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> followService.follow(null, null));
       assertThat(ex.getMessage()).contains("Parameters are null");
   }

    @Test
    @DisplayName("Unfollow fails when parameters are null")
    public void unfollowFailsWhenParametersAreNull(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> followService.unfollow(null, null));
        assertThat(ex.getMessage()).contains("Parameters are null");
    }
}
