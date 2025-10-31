package iteration1;

import Models.CreateUserRequest;
import Models.LoginUserRequest;
import Models.Roles;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import skelethon.EndPoints;
import skelethon.requesters.AdminSteps;
import skelethon.requesters.CrudRequester;
import skelethon.requesters.UserSteps;

import static Common.Common.*;

public class LoginUserTest {
    @Test
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRequest loginUserRequest = generate(LoginUserRequest.class);
        new CrudRequester(RequestSpecs.adminAuthSpec(), EndPoints.POST_LOGIN,
                ResponseSpecs.getOkStatusAndCheckHeader("Authorization", "Basic YWRtaW46YWRtaW4="))
                .post(loginUserRequest);

    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);
        LoginUserRequest loginUserRequest = generate(LoginUserRequest.class);
        loginUserRequest.setUsername(createUserRequest.getUsername());
        loginUserRequest.setPassword(createUserRequest.getPassword());
        UserSteps.userLogin(loginUserRequest);
    }
}
