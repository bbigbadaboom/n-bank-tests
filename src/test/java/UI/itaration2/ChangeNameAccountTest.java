package UI.itaration2;

import API.Models.CreateUserRequest;
import API.Models.UserChangeNameRequest;
import API.Models.UserGetHisProfileResponse;
import Common.Anotations.UserSession;
import Common.Extensions.UserSessionExtension;
import Common.Storage.SessionStorage;
import UI.BaseUiTest;
import UI.Pages.EditProfilePage;
import UI.Pages.UserPanel;
import UI.Utils.Alerts;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Alert;
import API.skelethon.requesters.AdminSteps;
import API.skelethon.requesters.UserSteps;

import java.time.Duration;

import static API.Common.Common.generate;
import static API.Common.Common.generateString;
import static UI.Pages.BasePage.authUser;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(UserSessionExtension.class)
public class ChangeNameAccountTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanCreateAccountTest() {
        UserChangeNameRequest userChangeNameRequest = generate(UserChangeNameRequest.class);
        new UserPanel().open().clickOnUserName().getPage(EditProfilePage.class)
                .createUser(userChangeNameRequest.getName())
                .checkAlert(Alerts.UPDATE_NAME);
        UserGetHisProfileResponse customer = SessionStorage.getSteps().userGetHisProfile();
        assertEquals(customer.getName(), userChangeNameRequest.getName());
    }

    @Test
    @UserSession
    public void userCantCreateAccountwithBadDataTest() {
        UserChangeNameRequest userChangeNameRequest = generate(UserChangeNameRequest.class);
        String ivalidName = generateString("[A-Za-z]{3}");
        userChangeNameRequest.setName(ivalidName);
        new UserPanel().open().clickOnUserName().getPage(EditProfilePage.class)
                .createUser(userChangeNameRequest.getName()).
                checkAlert(Alerts.UNSUCCES_UPDATE_NAME);
        UserGetHisProfileResponse getUserProfile = SessionStorage.getSteps().userGetHisProfile();
        assertNull(getUserProfile.getName());
    }
}
