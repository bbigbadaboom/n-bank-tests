package API.Models;

import Common.Anotations.RegexPattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest extends BaseModel {
    @RegexPattern("[A-Za-z0-9._-]{3,15}")
    private String username;
    @RegexPattern(type = FieldType.PASSWORD)
    private String password;
    @RegexPattern("USER")
    private String role;
}
