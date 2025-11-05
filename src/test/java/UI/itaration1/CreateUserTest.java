package UI.itaration1;

import API.Configs.Config;
import API.Models.CreateUserRequest;
import API.Models.CreateUserResponse;
import API.Models.LoginUserRequest;
import UI.Pages.AdminPanel;
import UI.BaseUiTest;
import UI.Utils.Alerts;
import com.codeborne.selenide.*;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import API.skelethon.requesters.AdminSteps;

import java.time.Duration;
import java.util.List;

import static API.Common.Common.generate;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest extends BaseUiTest {


    @Test
    public void createUserTest() {
        LoginUserRequest loginUserRequest = generate(LoginUserRequest.class);
        loginUserRequest.setUsername(API.Configs.Config.getProperties("admin.name"));
        loginUserRequest.setPassword(Config.getProperties("admin.pass"));
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        authUser(loginUserRequest);
        new AdminPanel().open().createUser(createUserRequest.getUsername(), createUserRequest.getPassword()).
        checkAlert(Alerts.USER_CREATED).getAllUsers().findBy(Condition.exactText(createUserRequest.getUsername() + "\nUSER"))
                .shouldBe(Condition.visible, Duration.ofSeconds(10));
        List<CreateUserResponse> users = AdminSteps.adminGetAllUsers();
        List<String> userNames = users.stream().map(CreateUserResponse::getUsername).toList();
        assertTrue(userNames.contains(createUserRequest.getUsername()));
    }

    @Test
    public void adminCantCreateUserTest() {
        LoginUserRequest loginUserRequest = generate(LoginUserRequest.class);
        loginUserRequest.setUsername(API.Configs.Config.getProperties("admin.name"));
        loginUserRequest.setPassword(Config.getProperties("admin.pass"));
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        createUserRequest.setUsername("a");
        authUser(loginUserRequest);
        new AdminPanel().open()
        .createUser(createUserRequest.getUsername(), createUserRequest.getPassword())
                .checkAlert(Alerts.USER_NOT_CREATED)
                .getAllUsers()
        .findBy(Condition.exactText(createUserRequest.getUsername() + "\nUSER")).shouldNotBe(Condition.visible, Duration.ofSeconds(5));
        List<CreateUserResponse> users = AdminSteps.adminGetAllUsers();
        List<String> userNames = users.stream().map(CreateUserResponse::getUsername).toList();
        assertFalse(userNames.contains(createUserRequest.getUsername()));
    }
}
