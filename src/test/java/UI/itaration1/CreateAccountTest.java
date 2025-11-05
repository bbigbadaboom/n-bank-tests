package UI.itaration1;

import Models.CreateUserRequest;
import Models.LoginUserRequest;
import Models.UserAccount;
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

import java.util.List;
import java.util.Map;

import static Common.Common.generate;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateAccountTest {

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
        $(Selectors.byText("➕ Create New Account")).click();
        Alert alert = switchTo().alert();
        assertTrue(alert.getText().contains("✅ New Account Created! Account Number:"));
        alert.accept();
        List<UserAccount> list = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertFalse(list.isEmpty()),
                () -> assertEquals(list.get(0).getBalance(), 0.0),
                () -> assertTrue(list.get(0).getTransactions().isEmpty())
        );
    }


}
