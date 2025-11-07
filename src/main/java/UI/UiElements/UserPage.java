package UI.UiElements;

import com.codeborne.selenide.SelenideElement;

public class UserPage extends BaseElement{
    private String username;
    private String role;
    public UserPage(SelenideElement element) {
        super(element);
        username = element.getText().split("\n")[0];
        role = element.getText().split("\n")[1];
    }
}
