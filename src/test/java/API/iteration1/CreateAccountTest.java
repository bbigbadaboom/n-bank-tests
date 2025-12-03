package API.iteration1;

import API.BaseTest;
import API.Models.CreateUserRequest;
import API.Models.UserAccount;
import DB.dao.AccountDao;
import DB.dao.comparison.DaoAndModelAssertions;
import DB.DataBaseSteps;
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
         assertEquals(list.get(0).getBalance(), 0.0);


//        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(list.get(0).getAccountNumber());
//        DaoAndModelAssertions.assertThat(list.get(0), accountDao).match();
    }
}


