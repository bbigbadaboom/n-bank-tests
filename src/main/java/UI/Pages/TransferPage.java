package UI.Pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class TransferPage extends BasePage<TransferPage>{
    @Override
    public String url() {
        return "/transfer";
    }

    private final SelenideElement accountChoise =$(Selectors.byText("-- Choose an account --"));
    private final SelenideElement transButton = $(Selectors.byText("\uD83D\uDE80 Send Transfer"));
    private final SelenideElement amountField = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private final SelenideElement recName= $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private final SelenideElement recAccount=  $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private final SelenideElement checkBox = $(Selectors.byId("confirmCheck"));



    public TransferPage doTransfer (int value, double balance, int secondValue ) {
        accountChoise.parent().selectOptionByValue(String.valueOf(value));
        recName.setValue("No name");
        recAccount.setValue("ACC" + secondValue);
        amountField.setValue(String.valueOf(balance));
        checkBox.click();
        transButton.click();
        return this;

    }
}
