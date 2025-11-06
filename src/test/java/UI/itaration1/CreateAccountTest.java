package UI.itaration1;

import API.Models.CreateUserRequest;
import API.Models.UserAccount;
import UI.BaseUiTest;
import UI.Pages.UserPanel;
import UI.Utils.Alerts;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import API.skelethon.requesters.AdminSteps;
import API.skelethon.requesters.UserSteps;

import java.util.List;

import static API.Common.Common.generate;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateAccountTest extends BaseUiTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);
        authUser(createUserRequest.getUsername(),createUserRequest.getPassword());
        new UserPanel().open().createUser()
                .checkAlert(Alerts.ACCOUNT_CREATED);
        List<UserAccount> list = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertFalse(list.isEmpty()),
                () -> assertEquals(list.get(0).getBalance(), 0.0),
                () -> assertTrue(list.get(0).getTransactions().isEmpty())
        );
    }


}
