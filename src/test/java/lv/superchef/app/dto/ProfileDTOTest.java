package lv.superchef.app.dto;

import lv.superchef.app.model.Profile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProfileDTO Unit Tests")
class ProfileDTOTest {

    @Test
    @DisplayName("Should correctly map Profile entity to ProfileDTO")
    void shouldMapProfileToProfileDto()
    {
        Profile profile = new Profile();
        profile.setDisplayName("Chef John");
        profile.setBio("Passionate home cook and food enthusiast.");
        profile.setProfileImageUrl("https://example.com/images/profile.jpg");

        ProfileDTO dto = ProfileDTO.mapToDto(profile);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(profile.getId());
        assertThat(dto.getDisplayName()).isEqualTo("Chef John");
        assertThat(dto.getBio()).isEqualTo("Passionate home cook and food enthusiast.");
        assertThat(dto.getProfileImageUrl()).isEqualTo("https://example.com/images/profile.jpg");
    }

    @Test
    @DisplayName("Should map Profile with null fields without throwing exceptions")
    void shouldMapProfileWithNullFields()
    {
        Profile profile = new Profile();

        ProfileDTO dto = ProfileDTO.mapToDto(profile);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getDisplayName()).isNull();
        assertThat(dto.getBio()).isNull();
        assertThat(dto.getProfileImageUrl()).isNull();
    }
}