package UI.itaration2;

import API.Models.CreateUserRequest;
import API.Models.DepositMoneyRequest;
import API.Models.UserAccount;
import UI.BaseUiTest;
import UI.Pages.TransferPage;
import UI.Pages.UserPanel;
import UI.Utils.Alerts;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import API.skelethon.requesters.AdminSteps;
import API.skelethon.requesters.UserSteps;

import java.util.Comparator;
import java.util.List;

import static API.Common.Common.generate;
import static API.Common.Common.randomDouble;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransferMoneyTest extends BaseUiTest {

    @Test
    public void userTransferMoney() {
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);

        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);

        authUser(createUserRequest.getUsername(), createUserRequest.getPassword());

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        UserAccount secondUserAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        int accountId = userAccount.getId();
        int secondAccountId = secondUserAccount.getId();
        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);
        UserSteps.userDepositMoney(depositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword());
        new UserPanel().open().doTransfer().getPage(TransferPage.class).doTransfer(accountId, amount, secondAccountId).
                checkAlert(Alerts.SUCCES_TRANSFER);

        List<UserAccount> userAccountswithTransfer =
                UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        List<UserAccount> userAccounts = userAccountswithTransfer.stream()
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccounts.get(0).getBalance(), balance - amount),
                () -> assertEquals(userAccounts.get(1).getBalance(), amount)
        );
    }

    @Test
    public void userCantTransferMoneyWithBadData() {
        double amount = randomDouble(2000, 5001);
        double balance = randomDouble(1000, 2000);

        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);

        authUser(createUserRequest.getUsername(), createUserRequest.getPassword());

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        UserAccount secondUserAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        int accountId = userAccount.getId();
        int secondAccountId = secondUserAccount.getId();
        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);
        UserSteps.userDepositMoney(depositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword());

        new UserPanel().open().doTransfer().getPage(TransferPage.class).doTransfer(accountId, amount, secondAccountId).
                checkAlert(Alerts.UNSUCCES_TRANSFER);
        List<UserAccount> userAccountswithTransfer =
                UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        List<UserAccount> userAccounts = userAccountswithTransfer.stream()
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccounts.get(0).getBalance(), balance),
                () -> assertEquals(userAccounts.get(1).getBalance(), 0.0)
        );
    }
}
