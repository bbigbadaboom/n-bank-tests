package UI.Pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class DepositPage extends BasePage<DepositPage>{
    @Override
    public String url() {
        return "/deposit";
    }

    private final SelenideElement accountChoise = $(Selectors.byText("-- Choose an account --"));
    private final SelenideElement depButton = $(Selectors.byText("\uD83D\uDCB5 Deposit"));
    private final SelenideElement balanceField = $(Selectors.byAttribute("placeholder", "Enter amount"));



    public DepositPage doDeposit (long value, double balance ) {
        depButton.shouldBe(Condition.visible);
        accountChoise.parent().selectOptionByValue(String.valueOf(value));
        balanceField.setValue(String.valueOf(balance));
        depButton.click();
        return this;
    }
}
