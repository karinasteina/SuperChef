package lv.superchef.app.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewFormDTO {
    @NotNull(message = "Please select a rating")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    private Integer rating;

    @NotBlank(message = "Review comment cannot be blank")
    @Size(max = 2000, message = "Review cannot exceed 2000 characters")
    private String comment;
}
