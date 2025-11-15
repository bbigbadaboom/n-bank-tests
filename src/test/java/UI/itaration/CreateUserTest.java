package UI.itaration;

import API.Models.CreateUserRequest;
import API.Models.CreateUserResponse;
import Common.Anotations.AdminSession;
import UI.Pages.AdminPanel;
import UI.BaseUiTest;
import UI.Utils.Alerts;
import org.junit.jupiter.api.Test;
import API.skelethon.Steps.AdminSteps;

import java.util.List;

import static Common.Common.generate;
import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest extends BaseUiTest {


    @Test
    @AdminSession
    public void createUserTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        new AdminPanel().open().createUser(createUserRequest.getUsername(), createUserRequest.getPassword()).
                checkAlert(Alerts.USER_CREATED).findUser(createUserRequest.getUsername());
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
                .checkAlert(Alerts.USER_NOT_CREATED);
        List<CreateUserResponse> users = AdminSteps.adminGetAllUsers();
        List<String> userNames = users.stream().map(CreateUserResponse::getUsername).toList();
        assertFalse(userNames.contains(createUserRequest.getUsername()));
    }
}
