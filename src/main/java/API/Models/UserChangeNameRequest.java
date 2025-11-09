package API.Models;

import Common.Anotations.RegexPattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChangeNameRequest extends BaseModel {
    @RegexPattern("[A-Za-z]{3} [A-Za-z]{3}")
    private String name;
}
