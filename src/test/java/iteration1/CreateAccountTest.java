package iteration1;

import Models.CreateUserRequest;
import Models.UserAccount;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import skelethon.EndPoints;
import skelethon.requesters.AdminSteps;
import skelethon.requesters.CrudRequester;
import skelethon.requesters.UserSteps;
import skelethon.requesters.ValidatedCrudRequester;

import static Common.Common.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


public class CreateAccountTest {
    @Test
    public void createUserAccountTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);

        UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());

        List<UserAccount> list = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertEquals(list.get(0).getBalance(), 0.0),
                () -> assertTrue(list.get(0).getTransactions().isEmpty())
        );
    }
}


