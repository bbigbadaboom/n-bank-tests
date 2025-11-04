package UI.itaration2;

import Models.CreateUserRequest;
import Models.LoginUserRequest;
import Models.UserChangeNameRequest;
import Models.UserGetHisProfileResponse;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import skelethon.EndPoints;
import skelethon.requesters.AdminSteps;
import skelethon.requesters.CrudRequester;
import skelethon.requesters.UserSteps;

import java.time.Duration;
import java.util.Map;

import static Common.Common.generate;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ChangeNameAccountTest {
    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.1.65:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));

    }

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        UserChangeNameRequest userChangeNameRequest = generate(UserChangeNameRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);

        LoginUserRequest loginUserRequest = generate(LoginUserRequest.class);
        loginUserRequest.setUsername(createUserRequest.getUsername());
        loginUserRequest.setPassword(createUserRequest.getPassword());

        String token = new CrudRequester(RequestSpecs.adminAuthSpec(), EndPoints.POST_LOGIN, ResponseSpecs.getOkStatus())
                .post(loginUserRequest)
                .extract()
                .header("Authorization");
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", token);
        Selenide.open("/dashboard");
        $(Selectors.byClassName("user-username")).click();
        $(Selectors.byText("✏️ Edit Profile")).shouldBe(Condition.visible);
        $(Selectors.byAttribute("placeholder", "Enter new name"))
                .shouldBe(Condition.visible, Duration.ofSeconds(9))
                .shouldBe(Condition.enabled)
                .setValue(userChangeNameRequest.getName());
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();
        Alert alert = switchTo().alert();
        assertEquals(alert.getText(), "✅ Name updated successfully!");
        alert.accept();
        UserGetHisProfileResponse customer = UserSteps.userGetHisProfile(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertEquals(customer.getName(), userChangeNameRequest.getName());
    }

    @Test
    public void userCantCreateAccountwithBadDataTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        UserChangeNameRequest userChangeNameRequest = generate(UserChangeNameRequest.class);
        userChangeNameRequest.setName("New");
        AdminSteps.adminCreateUser(createUserRequest);

        LoginUserRequest loginUserRequest = generate(LoginUserRequest.class);
        loginUserRequest.setUsername(createUserRequest.getUsername());
        loginUserRequest.setPassword(createUserRequest.getPassword());

        String token = new CrudRequester(RequestSpecs.adminAuthSpec(), EndPoints.POST_LOGIN, ResponseSpecs.getOkStatus())
                .post(loginUserRequest)
                .extract()
                .header("Authorization");
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", token);
        Selenide.open("/dashboard");
        $(Selectors.byClassName("user-username")).click();
        $(Selectors.byText("✏️ Edit Profile")).shouldBe(Condition.visible);
        $(Selectors.byAttribute("placeholder", "Enter new name"))
                .shouldBe(Condition.visible, Duration.ofSeconds(9))
                .shouldBe(Condition.enabled)
                .setValue(userChangeNameRequest.getName());
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();
        Alert alert = switchTo().alert();
        assertEquals(alert.getText(), "Name must contain two words with letters only");
        alert.accept();
        UserGetHisProfileResponse getUserProfile = UserSteps.userGetHisProfile(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertNull(getUserProfile.getName());
    }
}
