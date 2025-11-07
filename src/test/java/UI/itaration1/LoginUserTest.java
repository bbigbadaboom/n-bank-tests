package UI.itaration1;

import API.Configs.Config;
import API.Models.CreateUserRequest;
import API.Models.LoginUserRequest;
import Common.Anotations.AdminSession;
import UI.Pages.AdminPanel;
import UI.BaseUiTest;
import UI.Pages.LoginPage;
import UI.Pages.UserPanel;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import API.skelethon.requesters.AdminSteps;

import static API.Common.Common.generate;

public class LoginUserTest extends BaseUiTest {

    @Test
    public void adminCanLoginWithValidData() {
        LoginUserRequest createUserRequest = generate(LoginUserRequest.class);
        createUserRequest.setUsername(Config.getProperties("admin.name"));
        createUserRequest.setPassword(Config.getProperties("admin.pass"));
        new LoginPage().open().login(createUserRequest.getUsername(), createUserRequest.getPassword())
                .getPage(AdminPanel.class).getPanelText()
                .shouldBe(Condition.visible);
    }

    @Test
    public void userCanLoginWithValidData() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest .class);
        AdminSteps.adminCreateUser(createUserRequest);
        new LoginPage().open().login(createUserRequest.getUsername(), createUserRequest.getPassword())
                .getPage(UserPanel.class).getWelcomeText().shouldBe(Condition.visible).shouldBe(Condition.text("Welcome, noname"));
    }
}
