package UI.itaration1;

import API.Models.UserAccount;
import Common.Anotations.Browsers;
import Common.Anotations.UserSession;
import Common.Storage.SessionStorage;
import UI.BaseUiTest;
import UI.Pages.UserPanel;
import UI.Utils.Alerts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateAccountTest extends BaseUiTest {

    @Test
    @UserSession
    @Browsers({"firefox", "chrome"})
    public void userCanCreateAccountTest() {
        new UserPanel().open().createUser()
                .checkAlert(Alerts.ACCOUNT_CREATED);
        List<UserAccount> list = SessionStorage.getSteps().userGetHisAccounts();
        assertAll(
                () -> assertFalse(list.isEmpty()),
                () -> assertEquals(list.get(0).getBalance(), 0.0),
                () -> assertTrue(list.get(0).getTransactions().isEmpty())
        );
    }


}
