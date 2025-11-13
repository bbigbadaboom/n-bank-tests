package API.iteration1;

import API.BaseTest;
import API.Models.CreateUserRequest;
import API.Models.UserAccount;
import API.dao.AccountDao;
import API.dao.comparison.DaoAndModelAssertions;
import API.skelethon.Steps.DataBaseSteps;
import org.junit.jupiter.api.Test;
import API.skelethon.Steps.AdminSteps;

import API.skelethon.Steps.UserSteps;


import static Common.Common.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


public class CreateAccountTest extends BaseTest {
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
        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(list.get(0).getAccountNumber());
        DaoAndModelAssertions.assertThat(list.get(0), accountDao).match();
    }
}


