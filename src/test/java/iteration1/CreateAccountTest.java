package iteration1;

import Models.CreateUserRequest;
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
        String auth;
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();
        auth = new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest)
                .extract()
                .header("Authorization");

        new UserCreateAccountRequest(RequestSpecs.userAuthSpec(auth), ResponseSpecs.entityCreated())
                .post();
        new UserCreateAccountRequest(RequestSpecs.userAuthSpec(auth), ResponseSpecs.entityCreated())
                .post();

        List<UserAccount> list = new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(auth), ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .response().jsonPath().getList("", UserAccount.class);
        assertAll(
                () -> assertEquals(list.get(0).getBalance(), 0.0),
                () -> assertFalse(list.get(0).getTransactions().isEmpty())
        );
    }
}


