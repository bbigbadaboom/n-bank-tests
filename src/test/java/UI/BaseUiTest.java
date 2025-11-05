package UI;

import API.Configs.Config;
import API.Models.LoginUserRequest;
import API.Specs.RequestSpecs;
import UI.Utils.Alerts;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.Alert;

import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseUiTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperties("configuration.remote");
        Configuration.baseUrl = Config.getProperties("configuration.baseUrl");
        Configuration.browser = Config.getProperties("configuration.browser");
        Configuration.browserSize = Config.getProperties("configuration.browserSize");

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
    }
    public void authUser(String name, String pass) {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", RequestSpecs.getAuth(name, pass));
    }
    public void authUser(LoginUserRequest createUserRequest) {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);",
                RequestSpecs.getAuth(createUserRequest.getUsername(), createUserRequest.getPassword()));
    }
}
