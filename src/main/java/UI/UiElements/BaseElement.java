package UI.UiElements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public abstract class BaseElement {
    private final SelenideElement element;

    protected BaseElement(SelenideElement element) {
        this.element = element;
    }

    protected SelenideElement find(By selector){
        return element.find(selector);
    }

    protected SelenideElement find(String cssSelector){
        return element.find(cssSelector);
    }

    protected ElementsCollection findall(String cssSelector){
        return element.findAll(cssSelector);
    }
}
