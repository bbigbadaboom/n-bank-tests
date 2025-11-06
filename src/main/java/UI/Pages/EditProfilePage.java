package UI.Pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import java.time.Duration;

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

    public EditProfilePage createUser (String name) {
        panelText.shouldBe(Condition.visible);
        entryField
                .shouldBe(Condition.visible, Duration.ofSeconds(9))
                .shouldBe(Condition.enabled);
        entryField.setValue(name);
        saveChangesButton.click();
        return this;

    }


}
