package UI.Pages;

import API.Models.CreateUserRequest;
import API.Models.LoginUserRequest;
import API.Specs.RequestSpecs;
import UI.UiElements.BaseElement;
import UI.Utils.Alerts;
import com.codeborne.selenide.*;
import lombok.Getter;
import org.openqa.selenium.Alert;

import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public abstract class BasePage<T extends BasePage> {
    protected final SelenideElement userInput = $(Selectors.byAttribute("placeholder", "Username"));
    protected final SelenideElement userPass =$(Selectors.byAttribute("placeholder", "Password"));
    protected final SelenideElement depositButton = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    protected final SelenideElement transferButton = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));

    public abstract String url();
    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }
    public <T extends BasePage> T getPage(Class <T> pageClass) {
        return Selenide.page(pageClass);
    }

    public T checkAlert (Alerts message) {
        Alert alert = switchTo().alert();
        assertTrue(alert.getText().contains(message.getAlert()));
        alert.accept();
        return (T)this;
    }

    public static void authUser(String name, String pass) {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", RequestSpecs.getAuth(name, pass));
    }
    public static void authUser(LoginUserRequest createUserRequest) {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);",
                RequestSpecs.getAuth(createUserRequest.getUsername(), createUserRequest.getPassword()));
    }
    public static void authUser(CreateUserRequest createUserRequest) {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);",
                RequestSpecs.getAuth(createUserRequest.getUsername(), createUserRequest.getPassword()));
    }

    public <T extends BaseElement> List<T> generatePageElements(ElementsCollection elementsCollection,
                                                                   Function<SelenideElement, T> constructor) {
        return elementsCollection.stream().map(constructor).toList();
    }
}
