package UI.itaration2;

import API.Models.DepositMoneyRequest;
import API.Models.UserAccount;
import Common.Anotations.UserSession;
import Common.Anotations.WithAccounts;
import Common.Storage.AccountsStorage;
import Common.Storage.SessionStorage;
import UI.BaseUiTest;
import UI.Pages.TransferPage;
import UI.Pages.UserPanel;
import UI.Utils.Alerts;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static Common.Common.generate;
import static Common.Common.randomDouble;
import static org.junit.jupiter.api.Assertions.*;

public class TransferMoneyTest extends BaseUiTest {

    @Test
    @UserSession
    @WithAccounts(2)
    public void userTransferMoney() {
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);
        long accountId = AccountsStorage.getAccounts().get(0).getId();
        long secondAccountId = AccountsStorage.getAccounts().get(1).getId();
        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);
        SessionStorage.getSteps().userDepositMoney(depositMoneyRequest);
        new UserPanel().open().doTransfer().getPage(TransferPage.class).doTransfer(accountId, amount, secondAccountId).
                checkAlert(Alerts.SUCCES_TRANSFER);

        List<UserAccount> userAccountswithTransfer =
                SessionStorage.getSteps().userGetHisAccounts();
        List<UserAccount> userAccounts = userAccountswithTransfer.stream()
                .sorted(Comparator.comparingLong(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccounts.get(0).getBalance(), balance - amount),
                () -> assertEquals(userAccounts.get(1).getBalance(), amount)
        );
    }

    @Test
    @UserSession
    @WithAccounts(2)
    public void userCantTransferMoneyWithBadData() {
        double amount = randomDouble(2000, 5001);
        double balance = randomDouble(1000, 2000);

        long accountId = AccountsStorage.getAccounts().get(0).getId();
        long secondAccountId = AccountsStorage.getAccounts().get(1).getId();
        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);
        SessionStorage.getSteps().userDepositMoney(depositMoneyRequest);

        new UserPanel().open().doTransfer().getPage(TransferPage.class).doTransfer(accountId, amount, secondAccountId).
                checkAlert(Alerts.UNSUCCES_TRANSFER);
        List<UserAccount> userAccountswithTransfer =
                SessionStorage.getSteps().userGetHisAccounts();
        List<UserAccount> userAccounts = userAccountswithTransfer.stream()
                .sorted(Comparator.comparingLong(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccounts.get(0).getBalance(), balance),
                () -> assertEquals(userAccounts.get(1).getBalance(), 0.0)
        );
    }
}
