package API.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGetHisProfileResponse extends BaseModel {
    private int id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<UserAccount> accounts;
}
