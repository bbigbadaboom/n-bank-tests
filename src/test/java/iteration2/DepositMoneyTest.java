package iteration2;

import Models.*;
import Requests.AdminCreateUserRequest;
import Requests.UserCreateAccountRequest;
import Requests.UserDepositMoneyRequest;
import Requests.UserGetHisAccountsRequest;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static Common.Common.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositMoneyTest {
    private static Stream<Arguments> validData() {
        return Stream.of(
                Arguments.of("0.01 deposit", 0.01),
                Arguments.of("5000 deposit", 5000.0),
                Arguments.of("4999.99 deposit", 4999.99)

        );
    }

    private static Stream<Arguments> inValidBalanceData() {
        return Stream.of(
                Arguments.of("balance more than 5000", "5000.01", "Deposit amount cannot exceed 5000"),
                Arguments.of("balance 0", "0", "Deposit amount must be at least 0.01"),
                Arguments.of("balance -0.01", "-0.01", "Deposit amount must be at least 0.01")
        );
    }

    @ParameterizedTest(name = "{displayName} {0}")
    @MethodSource("validData")
    public void userDepositMoneyWithValidDataTest(String testName, Double amount){
        String name = generateName();
        String pass = generatePassword(10);
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();

        new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest).extract().as(CreateUserResponse.class);

        UserAccount userAccount = new UserCreateAccountRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.entityCreated())
                .post().extract().response().as(UserAccount.class);
        int accountId = userAccount.getId();
        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(amount).build();

        DepositMoneyResponse depositMoneyResponse = new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(depositMoneyRequest)
                .extract()
                .response().as(DepositMoneyResponse.class);
        assertAll(
                () -> assertEquals(depositMoneyResponse.getId(), accountId),
                () -> assertEquals(depositMoneyResponse.getBalance(), amount),
                () -> assertEquals(depositMoneyResponse.getTransactions().get(0).getType(), "DEPOSIT"),
                () -> assertEquals(depositMoneyResponse.getTransactions().get(0).getAmount(), amount)
        );

        List<UserAccount> userAccountwithDeposit = Arrays.asList((UserAccount[])new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .as(UserAccount.class.arrayType()));
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), amount),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 1)
        );
    }

    @Test
    public void userDepositMoneyWithValidDatawith2DepositsTest() {
        String name = generateName();
        String pass = generatePassword(10);
        double firstAmount = randomDouble(1, 5001);
        double secondAmount = randomDouble(1, 5001);
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();

        new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest).extract().as(CreateUserResponse.class);

        UserAccount userAccount = new UserCreateAccountRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.entityCreated())
                .post().extract().response().as(UserAccount.class);

        int accountId = userAccount.getId();
        DepositMoneyRequest firstDepositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(firstAmount).build();

        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(firstDepositMoneyRequest);

        DepositMoneyRequest secondDepositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(secondAmount).build();

        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(secondDepositMoneyRequest);

        List<UserAccount> userAccountwithDeposit = Arrays.asList((UserAccount[])new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .get()
                .extract().as(UserAccount.class.arrayType()));
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), firstAmount + secondAmount),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 2)
        );
    }

    @ParameterizedTest(name="{displayName} {0}")
    @MethodSource("inValidBalanceData")
    public void userDepositMoneyWithinValidBalanceDataTest(String testName, Double amount, String error) {
        String name = generateName();
        String pass = generatePassword(10);
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();

        new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest).extract().as(CreateUserResponse.class);

        UserAccount userAccount = new UserCreateAccountRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.entityCreated())
                .post().extract().response().as(UserAccount.class);

        int accountId = userAccount.getId();
        DepositMoneyRequest firstDepositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(amount).build();
        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getBadReqStatusWithMessage(error))
                .post(firstDepositMoneyRequest);

        List<UserAccount> userAccountwithDeposit = Arrays.asList((UserAccount[])new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .get()
                .extract().as(UserAccount.class.arrayType()));
        assertAll(
                () -> assertEquals(userAccountwithDeposit.get(0).getId(), accountId),
                () -> assertEquals(userAccountwithDeposit.get(0).getBalance(), 0.0),
                () -> assertEquals(userAccountwithDeposit.get(0).getTransactions().size(), 0)
        );
    }

    @Test
    public void userDepositMoneyWithinValidAccountTest() {
        String name = generateName();
        String pass = generatePassword(10);
        double balance = randomDouble(1, 5001);
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();

        new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest).extract().as(CreateUserResponse.class);

        DepositMoneyRequest firstDepositMoneyRequest = DepositMoneyRequest.builder().id(10).balance(balance).build();
        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getForbiddenStatus())
                .post(firstDepositMoneyRequest);
    }

}