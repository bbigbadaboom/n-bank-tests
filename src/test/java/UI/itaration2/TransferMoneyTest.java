package UI.itaration2;

import API.Models.CreateUserRequest;
import API.Models.DepositMoneyRequest;
import API.Models.UserAccount;
import Common.Anotations.UserSession;
import Common.Extensions.UserSessionExtension;
import Common.Storage.SessionStorage;
import UI.BaseUiTest;
import UI.Pages.TransferPage;
import UI.Pages.UserPanel;
import UI.Utils.Alerts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Alert;
import API.skelethon.requesters.AdminSteps;
import API.skelethon.requesters.UserSteps;

import java.util.Comparator;
import java.util.List;

import static API.Common.Common.generate;
import static API.Common.Common.randomDouble;
import static UI.Pages.BasePage.authUser;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(UserSessionExtension.class)
public class TransferMoneyTest extends BaseUiTest {

    @Test
    @UserSession
    public void userTransferMoney() {
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);

        UserAccount userAccount = SessionStorage.getSteps().userCreateAccount();
        UserAccount secondUserAccount = SessionStorage.getSteps().userCreateAccount();
        int accountId = userAccount.getId();
        int secondAccountId = secondUserAccount.getId();
        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);
        SessionStorage.getSteps().userDepositMoney(depositMoneyRequest);
        new UserPanel().open().doTransfer().getPage(TransferPage.class).doTransfer(accountId, amount, secondAccountId).
                checkAlert(Alerts.SUCCES_TRANSFER);

        List<UserAccount> userAccountswithTransfer =
                SessionStorage.getSteps().userGetHisAccounts();
        List<UserAccount> userAccounts = userAccountswithTransfer.stream()
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccounts.get(0).getBalance(), balance - amount),
                () -> assertEquals(userAccounts.get(1).getBalance(), amount)
        );
    }

    @Test
    @UserSession
    public void userCantTransferMoneyWithBadData() {
        double amount = randomDouble(2000, 5001);
        double balance = randomDouble(1000, 2000);

        UserAccount userAccount = SessionStorage.getSteps().userCreateAccount();
        UserAccount secondUserAccount = SessionStorage.getSteps().userCreateAccount();
        int accountId = userAccount.getId();
        int secondAccountId = secondUserAccount.getId();
        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);
        SessionStorage.getSteps().userDepositMoney(depositMoneyRequest);

        new UserPanel().open().doTransfer().getPage(TransferPage.class).doTransfer(accountId, amount, secondAccountId).
                checkAlert(Alerts.UNSUCCES_TRANSFER);
        List<UserAccount> userAccountswithTransfer =
                SessionStorage.getSteps().userGetHisAccounts();
        List<UserAccount> userAccounts = userAccountswithTransfer.stream()
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccounts.get(0).getBalance(), balance),
                () -> assertEquals(userAccounts.get(1).getBalance(), 0.0)
        );
    }
}
