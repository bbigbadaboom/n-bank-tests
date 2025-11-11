package UI.Pages;

import UI.UiElements.UserPage;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

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

    public List<UserPage> getAllUsers() {
        ElementsCollection elementsCollection = $(Selectors.byText("All Users")).parent().findAll("li");
        return generatePageElements(elementsCollection, UserPage::new);
    }

    public Optional<UserPage> findUser(String name) {
        return getAllUsers().stream().filter(userPage -> userPage.equals(name)).findFirst();


    }

}
