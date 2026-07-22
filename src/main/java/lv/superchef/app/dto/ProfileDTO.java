package lv.superchef.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.superchef.app.model.Profile;

@Getter
@Setter
@AllArgsConstructor
public class ProfileDTO {
    private Long id;
    private String displayName;
    private String bio;
    private String profileImageUrl;

    public static ProfileDTO mapToDto(Profile profile){
        return new ProfileDTO(profile.getId(), profile.getDisplayName(), profile.getBio(), profile.getProfileImageUrl());
    }

}
