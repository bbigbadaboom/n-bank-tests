package iteration1;

import Models.CreateUserRequest;
import Models.LoginUserRequest;
import Models.Roles;
import Requests.AdminCreateUserRequest;
import Requests.AdminLoginUserRequest;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

import static Common.Common.generateName;
import static Common.Common.generatePassword;

public class LoginUserTest {
    @Test
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRequest loginUserRequest = LoginUserRequest
                .builder()
                .username("admin")
                .password("admin")
                .build();
        new AdminLoginUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.getOkStatus())
                .post(loginUserRequest)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=");
    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        String name = generateName();
        String pass = generatePassword(10);
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();


        LoginUserRequest loginUserRequest = LoginUserRequest
                .builder()
                .username(name)
                .password(pass)
                .build();

        new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest);
        new AdminLoginUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.getOkStatus())
                .post(loginUserRequest);
    }
}
