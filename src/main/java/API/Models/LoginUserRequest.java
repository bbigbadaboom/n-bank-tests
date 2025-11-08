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
public class LoginUserRequest extends BaseModel {
    @RegexPattern("[A-Za-z0-9._-]{3,15}")
    private String username;
    @RegexPattern(type = FieldType.PASSWORD)
    private String password;
}
