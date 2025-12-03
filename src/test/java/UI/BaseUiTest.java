package UI;

import API.BaseTest;
import API.Configs.Config;
import Common.Extensions.AdminSessionExtension;
import Common.Extensions.BrowsersExtension;

import Common.Extensions.TimingExtension;
import Common.Extensions.UserExtension;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith(AdminSessionExtension.class)
@ExtendWith(UserExtension.class)
@ExtendWith(BrowsersExtension.class)


public class BaseUiTest extends BaseTest {
    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperties("configuration.remote");
        Configuration.baseUrl = Config.getProperties("baseUiUrl");
        Configuration.browser = Config.getProperties("configuration.browser");
        Configuration.browserSize = Config.getProperties("configuration.browserSize");
        Configuration.headless= true;
        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }
}
