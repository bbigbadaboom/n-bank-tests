package UI.Pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserPanel extends BasePage<UserPanel>{
    @Override
    public String url() {
        return "/dashboard";
    }
    private final SelenideElement panelText = $(Selectors.byText("User Dashboard"));
    private final SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private final SelenideElement createUserButton = $(Selectors.byText("➕ Create New Account"));
    private final SelenideElement usernameField =$(Selectors.byClassName("user-username"));

    public UserPanel createUser () {
        createUserButton.click();
        return this;
    }
    public UserPanel clickOnUserName () {
        usernameField.click();
        return this;
    }
    public UserPanel doDeposit () {
        depositButton.click();
        return this;
    }
    public UserPanel doTransfer () {
        transferButton.click();
        return this;
    }


}
