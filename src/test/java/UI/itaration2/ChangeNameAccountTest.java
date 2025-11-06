package UI.itaration2;

import API.Models.CreateUserRequest;
import API.Models.UserChangeNameRequest;
import API.Models.UserGetHisProfileResponse;
import UI.BaseUiTest;
import UI.Pages.EditProfilePage;
import UI.Pages.UserPanel;
import UI.Utils.Alerts;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import API.skelethon.requesters.AdminSteps;
import API.skelethon.requesters.UserSteps;

import java.time.Duration;

import static API.Common.Common.generate;
import static API.Common.Common.generateString;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ChangeNameAccountTest extends BaseUiTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        UserChangeNameRequest userChangeNameRequest = generate(UserChangeNameRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);
        authUser(createUserRequest.getUsername(), createUserRequest.getPassword());
        new UserPanel().open().clickOnUserName().getPage(EditProfilePage.class)
                .createUser(userChangeNameRequest.getName())
                .checkAlert(Alerts.UPDATE_NAME);
        UserGetHisProfileResponse customer = UserSteps.userGetHisProfile(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertEquals(customer.getName(), userChangeNameRequest.getName());
    }

    @Test
    public void userCantCreateAccountwithBadDataTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        UserChangeNameRequest userChangeNameRequest = generate(UserChangeNameRequest.class);
        String ivalidName = generateString("[A-Za-z]{3}");
        userChangeNameRequest.setName(ivalidName);
        AdminSteps.adminCreateUser(createUserRequest);
        authUser(createUserRequest.getUsername(), createUserRequest.getPassword());
        new UserPanel().open().clickOnUserName().getPage(EditProfilePage.class)
                .createUser(userChangeNameRequest.getName()).
                checkAlert(Alerts.UNSUCCES_UPDATE_NAME);
        UserGetHisProfileResponse getUserProfile = UserSteps.userGetHisProfile(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertNull(getUserProfile.getName());
    }
}
