package UI.itaration1;

import Models.CreateUserRequest;
import Models.CreateUserResponse;
import Models.LoginUserRequest;
import com.codeborne.selenide.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import skelethon.requesters.AdminSteps;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static Common.Common.generate;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest {

    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.1.65:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));

    }

    @Test
    public void createUserTest() {
        LoginUserRequest loginUserRequestUserRequest = generate(LoginUserRequest.class);
        loginUserRequestUserRequest.setUsername("admin");
        loginUserRequestUserRequest.setPassword("admin");
        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(loginUserRequestUserRequest.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(loginUserRequestUserRequest.getPassword());
        $("button").click();
        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(createUserRequest.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(createUserRequest.getPassword());
        $(Selectors.byText("Add User")).click();
        Alert alert = switchTo().alert();
        assertEquals(alert.getText(), "✅ User created successfully!");
        alert.accept();
        ElementsCollection allUsers = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsers.findBy(Condition.exactText(createUserRequest.getUsername() + "\nUSER")).shouldBe(Condition.visible, Duration.ofSeconds(5));
        List<CreateUserResponse> users = AdminSteps.adminGetAllUsers();
        List<String> userNames = users.stream().map(CreateUserResponse::getUsername).toList();
        assertTrue(userNames.contains(createUserRequest.getUsername()));
    }

    @Test
    public void adminCantCreateUserTest() {
        LoginUserRequest loginUserRequestUserRequest = generate(LoginUserRequest.class);
        loginUserRequestUserRequest.setUsername("admin");
        loginUserRequestUserRequest.setPassword("admin");
        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(loginUserRequestUserRequest.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(loginUserRequestUserRequest.getPassword());
        $("button").click();
        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        createUserRequest.setUsername("a");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(createUserRequest.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(createUserRequest.getPassword());
        $(Selectors.byText("Add User")).click();
        Alert alert = switchTo().alert();
        assertTrue(alert.getText().contains("Username must be between 3 and 15 characters"));
        alert.accept();
        ElementsCollection allUsers = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsers.findBy(Condition.exactText(createUserRequest.getUsername() + "\nUSER")).shouldNotBe(Condition.visible, Duration.ofSeconds(5));
        List<CreateUserResponse> users = AdminSteps.adminGetAllUsers();
        List<String> userNames = users.stream().map(CreateUserResponse::getUsername).toList();
        assertFalse(userNames.contains(createUserRequest.getUsername()));

    }
}
