package UI.itaration2;

import API.Models.UserChangeNameRequest;
import API.Models.UserGetHisProfileResponse;
import Common.Anotations.UserSession;
import Common.Storage.SessionStorage;
import UI.BaseUiTest;
import UI.Pages.EditProfilePage;
import UI.Pages.UserPanel;
import UI.Utils.Alerts;
import org.junit.jupiter.api.Test;

import static Common.Common.generate;
import static Common.Common.generateString;
import static UI.Pages.BasePage.authUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ChangeNameAccountTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanChangeNametTest() {
        UserChangeNameRequest userChangeNameRequest = generate(UserChangeNameRequest.class);
        new UserPanel().open().clickOnUserName().getPage(EditProfilePage.class)
                .changeName(userChangeNameRequest.getName())
                .checkAlert(Alerts.UPDATE_NAME);
        UserGetHisProfileResponse customer = SessionStorage.getSteps().userGetHisProfile();
        assertEquals(customer.getName(), userChangeNameRequest.getName());
    }

    @Test
    @UserSession
    public void userCantChangeNametTest() {
        UserChangeNameRequest userChangeNameRequest = generate(UserChangeNameRequest.class);
        String ivalidName = generateString("[A-Za-z]{3}");
        userChangeNameRequest.setName(ivalidName);
        new UserPanel().open().clickOnUserName().getPage(EditProfilePage.class)
                .changeName(userChangeNameRequest.getName()).
                checkAlert(Alerts.UNSUCCES_UPDATE_NAME);
        UserGetHisProfileResponse getUserProfile = SessionStorage.getSteps().userGetHisProfile();
        assertNull(getUserProfile.getName());
    }
}
