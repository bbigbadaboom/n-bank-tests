package UI.Pages;

import UI.Utils.Alerts;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.Alert;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public abstract class BasePage<T extends BasePage> {
    public abstract String url();
    public T open() {
       return Selenide.open(url(), (Class<T>) this.getClass());
    }
    public <T extends BasePage> T getPage(Class <T> pageClass) {
        return Selenide.page(pageClass);
    }
    protected final SelenideElement userInput = $(Selectors.byAttribute("placeholder", "Username"));
    protected final SelenideElement userPass =$(Selectors.byAttribute("placeholder", "Password"));
    protected final SelenideElement depositButton = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    protected final SelenideElement transferButton = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));

    public T checkAlert (Alerts message) {
        Alert alert = switchTo().alert();
        assertTrue(alert.getText().contains(message.getAlert()));
        alert.accept();
        return (T)this;
    }
}
