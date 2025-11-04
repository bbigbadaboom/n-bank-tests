package UI.itaration2;

import Models.CreateUserRequest;
import Models.DepositMoneyRequest;
import Models.LoginUserRequest;
import Models.UserAccount;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static Common.Common.generate;
import static Common.Common.randomDouble;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransferMoneyTest {

    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.1.65:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));

    }

    @Test
    public void userTransferMoney() {
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);

        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);

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
        UserAccount secondUserAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        int accountId = userAccount.getId();
        int secondAccountId = secondUserAccount.getId();

        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);

        UserSteps.userDepositMoney(depositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword());

        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).shouldBe(Condition.visible);
        $(Selectors.byText("-- Choose an account --")).parent().selectOptionByValue(String.valueOf(accountId));
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys("No name");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number"))
                .sendKeys("ACC" + secondAccountId);
        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .sendKeys(String.valueOf(amount));
        $(Selectors.byId("confirmCheck")).click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();
        Alert alert = switchTo().alert();
        assertTrue(alert.getText().contains("✅ Successfully transferred $" + amount + " to account "));
        List<UserAccount> userAccountswithTransfer =
                UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        List<UserAccount> userAccounts = userAccountswithTransfer.stream()
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccounts.get(0).getBalance(), balance - amount),
                () -> assertEquals(userAccounts.get(1).getBalance(), amount)
        );
    }

    @Test
    public void userCantTransferMoneyWithBadData() {
        double amount = randomDouble(2000, 5001);
        double balance  = randomDouble(1000, 2000);

        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);

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
        UserAccount secondUserAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        int accountId = userAccount.getId();
        int secondAccountId = secondUserAccount.getId();

        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);

        UserSteps.userDepositMoney(depositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword());

        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).shouldBe(Condition.visible);
        $(Selectors.byText("-- Choose an account --")).parent().selectOptionByValue(String.valueOf(accountId));
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys("No name");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number"))
                .sendKeys("ACC" + secondAccountId);
        $(Selectors.byAttribute("placeholder", "Enter amount"))
                .sendKeys(String.valueOf(amount));
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();
        Alert alert = switchTo().alert();
        assertTrue(alert.getText().contains("❌ Please fill all fields and confirm."));
        List<UserAccount> userAccountswithTransfer =
                UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        List<UserAccount> userAccounts = userAccountswithTransfer.stream()
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccounts.get(0).getBalance(), balance),
                () -> assertEquals(userAccounts.get(1).getBalance(), 0.0)
        );
    }
}
