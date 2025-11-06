package UI.itaration2;

import API.Models.CreateUserRequest;
import API.Models.DepositMoneyRequest;
import API.Models.UserAccount;
import UI.BaseUiTest;
import UI.Pages.DepositPage;
import UI.Pages.UserPanel;
import UI.Utils.Alerts;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import API.skelethon.requesters.AdminSteps;
import API.skelethon.requesters.UserSteps;

import java.util.List;

import static API.Common.Common.generate;
import static org.junit.jupiter.api.Assertions.*;

public class DepositMoneyTest extends BaseUiTest {

    @Test
    public void userDepositMoneyTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);
        DepositMoneyRequest dep = generate(DepositMoneyRequest.class);

        authUser(createUserRequest.getUsername(), createUserRequest.getPassword());
        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        int accountId = userAccount.getId();
        new UserPanel().open().doDeposit().getPage(DepositPage.class).doDeposit(accountId, dep.getBalance())
                .checkAlert(Alerts.SUCCES_DEPOSIT);
        List<UserAccount> userAccountwithDeposit = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), dep.getBalance()),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 1)
        );
    }

    @Test
    public void userCantDepositMoneyWithInvalidTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);
        DepositMoneyRequest dep = generate(DepositMoneyRequest.class);
        dep.setBalance(-10);
        authUser(createUserRequest.getUsername(), createUserRequest.getPassword());
        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        int accountId = userAccount.getId();
        new UserPanel().open().doDeposit().getPage(DepositPage.class).doDeposit(accountId, dep.getBalance())
                .checkAlert(Alerts.UNSUCCES_DEPOSIT);
        List<UserAccount> userAccountwithDeposit = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), 0.0),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 0)
        );
    }
}
