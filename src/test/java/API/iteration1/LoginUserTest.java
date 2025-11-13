package API.iteration1;

import API.BaseTest;
import API.Models.CreateUserRequest;
import API.Models.LoginUserRequest;
import API.Specs.RequestSpecs;
import API.Specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import API.skelethon.EndPoints;
import API.skelethon.requesters.AdminSteps;
import API.skelethon.requesters.CrudRequester;
import API.skelethon.requesters.UserSteps;

import static Common.Common.*;

public class LoginUserTest extends BaseTest {
    @Test
    public void adminCanGenerateAuthTokenTest() {
        LoginUserRequest loginUserRequest = generate(LoginUserRequest.class);
        loginUserRequest.setUsername("admin");
        loginUserRequest.setPassword("admin");
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
