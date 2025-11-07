package UI.itaration2;

import API.Models.CreateUserRequest;
import API.Models.DepositMoneyRequest;
import API.Models.UserAccount;
import Common.Anotations.UserSession;
import Common.Extensions.UserSessionExtension;
import Common.Storage.SessionStorage;
import UI.BaseUiTest;
import UI.Pages.DepositPage;
import UI.Pages.UserPanel;
import UI.Utils.Alerts;
import org.junit.jupiter.api.Test;
import API.skelethon.requesters.AdminSteps;
import API.skelethon.requesters.UserSteps;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static API.Common.Common.generate;
import static API.Common.Common.randomDouble;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(UserSessionExtension.class)
public class DepositMoneyTest extends BaseUiTest {

    @Test
    @UserSession
    public void userDepositMoneyTest() {
        DepositMoneyRequest dep = generate(DepositMoneyRequest.class);
        UserAccount userAccount = SessionStorage.getSteps().userCreateAccount();
        int accountId = userAccount.getId();
        new UserPanel().open().doDeposit().getPage(DepositPage.class).doDeposit(accountId, dep.getBalance())
                .checkAlert(Alerts.SUCCES_DEPOSIT);
        List<UserAccount> userAccountwithDeposit = SessionStorage.getSteps().userGetHisAccounts();
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), dep.getBalance()),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 1)
        );
    }

    @Test
    @UserSession
    public void userCantDepositMoneyWithInvalidTest() {
        double balance = randomDouble(-10, -5);
        DepositMoneyRequest dep = generate(DepositMoneyRequest.class);
        dep.setBalance(balance);
        UserAccount userAccount = SessionStorage.getSteps().userCreateAccount();
        int accountId = userAccount.getId();
        new UserPanel().open().doDeposit().getPage(DepositPage.class).doDeposit(accountId, dep.getBalance())
                .checkAlert(Alerts.UNSUCCES_DEPOSIT);
        List<UserAccount> userAccountwithDeposit = SessionStorage.getSteps().userGetHisAccounts();
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), 0.0),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 0)
        );
    }
}
