package iteration1;

import Models.CreateUserRequest;
import Models.CreateUserResponse;
import Models.Roles;
import Models.UserAccount;
import Requests.AdminCreateUserRequest;
import Requests.UserCreateAccountRequest;
import Requests.UserGetHisAccountsRequest;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

import static Generates.Common.generateName;
import static Generates.Common.generatePassword;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


public class CreateAccountTest {
    @Test
    public void createUserAccountTest() {
        String name = generateName();
        String pass = generatePassword(10);
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();

        new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest).extract().as(CreateUserResponse.class);

        new UserCreateAccountRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.entityCreated())
                .post();

        List<UserAccount> list = new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .response().jsonPath().getList("", UserAccount.class);
        assertAll(
                () -> assertEquals(list.get(0).getBalance(), 0.0),
                () -> assertTrue(list.get(0).getTransactions().isEmpty())
        );
    }
}


