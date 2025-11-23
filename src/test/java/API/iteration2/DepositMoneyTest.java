package API.iteration2;

import API.BaseTest;
import API.Models.CreateUserRequest;
import API.Models.DepositMoneyRequest;
import API.Models.DepositMoneyResponse;
import API.Models.UserAccount;
import DB.dao.AccountDao;
import DB.dao.comparison.DaoAndModelAssertions;
import DB.DataBaseSteps;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import API.skelethon.Steps.AdminSteps;
import API.skelethon.Steps.UserSteps;

import java.util.List;
import java.util.stream.Stream;

import static Common.Common.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositMoneyTest extends BaseTest {
    private static Stream<Arguments> validData()  {
        return Stream.of(
                Arguments.of("0.01 deposit", 0.01),
                Arguments.of("5000 deposit", 5000.0),
                Arguments.of("4999.99 deposit", 4999.99)

        );
    }

    private static Stream<Arguments> inValidBalanceData() {
        return Stream.of(
                Arguments.of("balance more than 5000", "5000.01", "Deposit amount exceeds the 5000 limit"),
                Arguments.of("balance 0", "0", "Invalid account or amount"),
                Arguments.of("balance -0.01", "-0.01", "Invalid account or amount")
        );
    }
    @ParameterizedTest(name = "{displayName} {0}")
    @MethodSource("validData")
    @Disabled("баг")
    public void userDepositMoneyWithValidDataTest(String testName, Double amount){
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);

        AdminSteps.adminCreateUser(createUserRequest);

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        long accountId = userAccount.getId();

        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setAccountId(accountId);
        depositMoneyRequest.setAmount(amount);

        DepositMoneyResponse depositMoneyResponse = UserSteps.userDepositMoney(depositMoneyRequest,
                createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertEquals(depositMoneyResponse.getId(), accountId),
                () -> assertEquals(depositMoneyResponse.getBalance(), amount)
        );

        List<UserAccount> userAccountwithDeposit = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), amount),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 1)
        );

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(userAccountwithDeposit.get(0).getAccountNumber());
        DaoAndModelAssertions.assertThat(userAccountwithDeposit.get(0), accountDao).match();


    }

    @Test
    @Disabled("баг")
    public void userDepositMoneyWithValidDatawith2DepositsTest() {

        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);

        AdminSteps.adminCreateUser(createUserRequest);

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        long accountId = userAccount.getId();
        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setAccountId(accountId);

        UserSteps.userDepositMoney(depositMoneyRequest,createUserRequest.getUsername(), createUserRequest.getPassword());

        DepositMoneyRequest secondDepositMoneyRequest = generate(DepositMoneyRequest.class);
        secondDepositMoneyRequest.setAccountId(accountId);
        UserSteps.userDepositMoney(secondDepositMoneyRequest,createUserRequest.getUsername(), createUserRequest.getPassword());


        List<UserAccount> userAccountwithDeposit =
                UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), depositMoneyRequest.getAmount() + secondDepositMoneyRequest.getAmount()),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 2)
        );
    }

    @ParameterizedTest(name="{displayName} {0}")
    @MethodSource("inValidBalanceData")
    public void userDepositMoneyWithinValidBalanceDataTest(String testName, Double amount, String error) {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);

        AdminSteps.adminCreateUser(createUserRequest);

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());

        long accountId = userAccount.getId();
        DepositMoneyRequest firstDepositMoneyRequest = generate(DepositMoneyRequest.class);
        firstDepositMoneyRequest.setAccountId(accountId);
        firstDepositMoneyRequest.setAmount(amount);

        UserSteps.userDepositMoneyWithBadData(firstDepositMoneyRequest,createUserRequest.getUsername(), createUserRequest.getPassword(), error);

        List<UserAccount> userAccountwithDeposit =UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), 0.0),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 0)
        );

        AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(userAccountwithDeposit.get(0).getAccountNumber());
        DaoAndModelAssertions.assertThat(userAccountwithDeposit.get(0), accountDao).match();
    }

    @Test
    public void userDepositMoneyWithinValidAccountTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);
        DepositMoneyRequest firstDepositMoneyRequest = generate(DepositMoneyRequest.class);
        UserSteps.userDepositMoneyWithNoAcces(firstDepositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword());
    }

}