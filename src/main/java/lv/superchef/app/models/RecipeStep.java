package lv.superchef.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStep
{
    private Long id;
    private int stepNumber;
    private String instruction;
}
