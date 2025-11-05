package UI.itaration2;

import Models.*;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import skelethon.EndPoints;
import skelethon.requesters.AdminSteps;
import skelethon.requesters.CrudRequester;
import skelethon.requesters.UserSteps;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static Common.Common.generate;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class DepositMoneyTest {
    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.1.65:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));

    }

    @Test
    public void userDepositMoneyTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);
        DepositMoneyRequest dep = generate(DepositMoneyRequest.class);

        LoginUserRequest loginUserRequest = generate(LoginUserRequest.class);
        loginUserRequest.setUsername(createUserRequest.getUsername());
        loginUserRequest.setPassword(createUserRequest.getPassword());

        String token = new CrudRequester(RequestSpecs.adminAuthSpec(), EndPoints.POST_LOGIN, ResponseSpecs.getOkStatus())
                .post(loginUserRequest)
                .extract()
                .header("Authorization");
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", token);
        Selenide.open("/dashboard");
        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        int accountId = userAccount.getId();
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).shouldBe(Condition.visible);

        $(Selectors.byText("-- Choose an account --")).parent().selectOptionByValue(String.valueOf(accountId));
        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .sendKeys(String.valueOf(dep.getBalance()));
        $(Selectors.byText("\uD83D\uDCB5 Deposit")).click();
        Alert alert = switchTo().alert();
        assertTrue(alert.getText().contains("✅ Successfully deposited $" + dep.getBalance()+ " to account"));
        List<UserAccount> userAccountwithDeposit = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), dep.getBalance()),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 1)
        );
    }

    @Test
    public void userCantDepositMoneyWithInvalidTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);
        DepositMoneyRequest dep = generate(DepositMoneyRequest.class);
        dep.setBalance(-10);

        LoginUserRequest loginUserRequest = generate(LoginUserRequest.class);
        loginUserRequest.setUsername(createUserRequest.getUsername());
        loginUserRequest.setPassword(createUserRequest.getPassword());

        String token = new CrudRequester(RequestSpecs.adminAuthSpec(), EndPoints.POST_LOGIN, ResponseSpecs.getOkStatus())
                .post(loginUserRequest)
                .extract()
                .header("Authorization");
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", token);
        Selenide.open("/dashboard");
        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        int accountId = userAccount.getId();
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).shouldBe(Condition.visible);

        $(Selectors.byText("-- Choose an account --")).parent().selectOptionByValue(String.valueOf(accountId));
        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .sendKeys(String.valueOf(dep.getBalance()));
        $(Selectors.byText("\uD83D\uDCB5 Deposit")).click();
        Alert alert = switchTo().alert();
        assertTrue(alert.getText().contains("❌ Please enter a valid amount."));
        List<UserAccount> userAccountwithDeposit = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), 0.0),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 0)
        );
    }
}
