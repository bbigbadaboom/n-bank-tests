package UI.Pages;

import UI.Utils.RetryUtils;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import java.time.Duration;

import static UI.Utils.RetryUtils.retry;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class EditProfilePage extends BasePage<EditProfilePage>{
    @Override
    public String url() {
        return "edit-profile";
    }
    private final SelenideElement panelText = $(Selectors.byText("✏️ Edit Profile"));
    private final SelenideElement entryField = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private final SelenideElement saveChangesButton = $(Selectors.byText("\uD83D\uDCBE Save Changes"));

    public EditProfilePage changeName (String name) {
        panelText.shouldBe(Condition.visible);
        retry(
                ()-> entryField.getValue().contains(name),
                () -> entryField.sendKeys(name),
                3,
                1000);
        saveChangesButton.click();
        return this;

    }


}
