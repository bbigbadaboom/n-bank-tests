package UI.itaration1;

import API.Configs.Config;
import API.Models.CreateUserRequest;
import API.Models.CreateUserResponse;
import API.Models.LoginUserRequest;
import Common.Anotations.AdminSession;
import Common.Extensions.AdminSessionExtension;
import UI.Pages.AdminPanel;
import UI.BaseUiTest;
import UI.Utils.Alerts;
import com.codeborne.selenide.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Alert;
import API.skelethon.requesters.AdminSteps;

import java.time.Duration;
import java.util.List;

import static API.Common.Common.generate;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest extends BaseUiTest {


    @Test
    @AdminSession
    public void createUserTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        new AdminPanel().open().createUser(createUserRequest.getUsername(), createUserRequest.getPassword()).
        checkAlert(Alerts.USER_CREATED).getAllUsers().stream().anyMatch(userPage -> userPage.equals(createUserRequest.getUsername()));
        List<CreateUserResponse> users = AdminSteps.adminGetAllUsers();
        List<String> userNames = users.stream().map(CreateUserResponse::getUsername).toList();
        assertTrue(userNames.contains(createUserRequest.getUsername()));
    }

    @Test
    @AdminSession
    public void adminCantCreateUserTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        createUserRequest.setUsername("a");
        new AdminPanel().open()
        .createUser(createUserRequest.getUsername(), createUserRequest.getPassword())
                .checkAlert(Alerts.USER_NOT_CREATED)
                .getAllUsers()
        .stream().anyMatch(userPage -> userPage.equals(createUserRequest.getUsername()));
        List<CreateUserResponse> users = AdminSteps.adminGetAllUsers();
        List<String> userNames = users.stream().map(CreateUserResponse::getUsername).toList();
        assertFalse(userNames.contains(createUserRequest.getUsername()));
    }
}
