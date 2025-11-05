package UI.itaration1;

import Models.CreateUserRequest;
import Models.LoginUserRequest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import skelethon.requesters.AdminSteps;

import java.util.Map;

import static Common.Common.generate;
import static com.codeborne.selenide.Selenide.$;

public class LoginUserTest {
    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.1.65:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enalbeVNC", true, "enableLog", true));

    }

    @Test
    public void adminCanLoginWithValidData() {
        LoginUserRequest createUserRequest = generate(LoginUserRequest.class);
        createUserRequest.setUsername("admin");
        createUserRequest.setPassword("admin");
        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(createUserRequest.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(createUserRequest.getPassword());
        $("button").click();
        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);
    }

    @Test
    public void userCanLoginWithValidData() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest .class);
        AdminSteps.adminCreateUser(createUserRequest);

        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(createUserRequest.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(createUserRequest.getPassword());
        $("button").click();
        $(Selectors.byText("User Dashboard")).shouldBe(Condition.visible);
        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldBe(Condition.text("Welcome, noname"));
    }
}
