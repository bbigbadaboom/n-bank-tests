package API.iteration1;

import API.Models.CreateUserRequest;
import API.Models.UserAccount;
import org.junit.jupiter.api.Test;
import API.skelethon.requesters.AdminSteps;

import API.skelethon.requesters.UserSteps;


import static API.Common.Common.*;
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


