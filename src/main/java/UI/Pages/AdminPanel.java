package UI.Pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class AdminPanel extends BasePage<AdminPanel> {
    @Override
    public String url() {
        return "/admin";
    }
    private final SelenideElement panelText = $(Selectors.byText("Admin Panel"));
    private final SelenideElement createButton = $(Selectors.byText("Add User"));

    public AdminPanel createUser (String name, String pass) {
        panelText.shouldBe(Condition.visible);
        userInput.sendKeys(name);
        userPass.sendKeys(pass);
        createButton.click();
        return this;
    }

    public ElementsCollection getAllUsers() {

        return $(Selectors.byText("All Users")).parent().findAll("li");
    }

}
