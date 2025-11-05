package UI.Pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage>{
    @Override
    public String url() {
        return "/login";
    }
    private final SelenideElement loginButton = $("button");

    public LoginPage login (String name, String pass) {
        userInput.sendKeys(name);
        userPass.sendKeys(pass);
        loginButton.click();
        return this;

    }
}
