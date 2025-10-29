package iteration2;

import Models.*;
import Requests.*;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static Generates.Common.generateName;
import static Generates.Common.generatePassword;
import static org.junit.jupiter.api.Assertions.*;

public class TransferMoneyTest {
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );
    }

    private static Stream<Arguments> inValidAmountData() {
        return Stream.of(
                Arguments.of("amount more than 10000", "10000.01", "Transfer amount cannot exceed 10000"),
                Arguments.of("amount 0", "0", "Transfer amount must be at least 0.01"),
                Arguments.of("amount -0.01", "-0.01", "Transfer amount must be at least 0.01")
        );
    }


    @Test
    public void transferMoneyBetweenUsersAccountsTest() {
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
        UserAccount secondUserAccount = new UserCreateAccountRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.entityCreated())
                .post().extract().response().as(UserAccount.class);

        int secondAccountId = secondUserAccount.getId();

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(3000.0).build();

        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(depositMoneyRequest);

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(accountId)
                .receiverAccountId(secondAccountId)
                .amount(0.01)
                .build();

        TransferMoneyResponse transferMoneyResponse = new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(transferMoneyRequest)
                .extract()
                .response().as(TransferMoneyResponse.class);

        assertAll(
                () -> assertEquals(transferMoneyResponse.getSenderAccountId(), accountId),
                () -> assertEquals(transferMoneyResponse.getMessage(), "Transfer successful"),
                () -> assertEquals(transferMoneyResponse.getAmount(), 0.01),
                () -> assertEquals(transferMoneyResponse.getReceiverAccountId(), secondAccountId)
        );
        List<UserAccount> userAccountswithTransfer = new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .response().jsonPath().getList("", UserAccount.class).stream()
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccountswithTransfer.get(0).getBalance(), 2999.99),
                () -> assertEquals(userAccountswithTransfer.get(1).getBalance(), 0.01)
        );
    }

    @Test
    public void transferMoneyBetweenDifferentUsersAccounts() {
        String name = generateName();
        String pass = generatePassword(10);
        String secondName = generateName();
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();

        new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest).extract().as(CreateUserResponse.class);

        CreateUserRequest createSecondUserRequest = CreateUserRequest
                .builder()
                .username(secondName)
                .password(pass)
                .role(Roles.USER.toString())
                .build();

        new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createSecondUserRequest).extract().as(CreateUserResponse.class);

        UserAccount userAccount = new UserCreateAccountRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.entityCreated())
                .post().extract().response().as(UserAccount.class);
        int accountId = userAccount.getId();

        UserAccount secondUserAccount = new UserCreateAccountRequest(RequestSpecs.userAuthSpec(secondName, pass), ResponseSpecs.entityCreated())
                .post().extract().response().as(UserAccount.class);

        int secondAccountId = secondUserAccount.getId();

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(5000.0).build();

        for (int i = 0; i < 3; i++) {
            new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                    .post(depositMoneyRequest);
        }

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(accountId)
                .receiverAccountId(secondAccountId)
                .amount(10000)
                .build();

        TransferMoneyResponse transferMoneyResponse = new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(transferMoneyRequest)
                .extract()
                .response().as(TransferMoneyResponse.class);

        assertAll(
                () -> assertEquals(transferMoneyResponse.getSenderAccountId(), accountId),
                () -> assertEquals(transferMoneyResponse.getMessage(), "Transfer successful"),
                () -> assertEquals(transferMoneyResponse.getAmount(), 10000.0),
                () -> assertEquals(transferMoneyResponse.getReceiverAccountId(), secondAccountId)
        );

        List<UserAccount> firstUserAccountswithTransfer = new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass),
                ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .response().jsonPath().getList("", UserAccount.class);
        assertEquals(firstUserAccountswithTransfer.get(0).getBalance(), 5000.0);

        List<UserAccount> secondUserAccountswithTransfer = new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(secondName, pass),
                ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .response().jsonPath().getList("", UserAccount.class);
        assertEquals(secondUserAccountswithTransfer.get(0).getBalance(), 10000.0);
    }

    @Test
    public void transferMoneyFromInvalidAccountTest() {
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

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(3000.0).build();

        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(depositMoneyRequest);

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(-10)
                .receiverAccountId(accountId)
                .amount(0.01)
                .build();

        new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getForbiddenStatus())
                .post(transferMoneyRequest);
    }

    @Test
    public void transferMoneyToInvalidAccountTest() {
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

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(3000.0).build();

        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(depositMoneyRequest);
        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(accountId)
                .receiverAccountId(-10)
                .amount(0.01)
                .build();
        new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass),
                ResponseSpecs.getBadReqStatusWithMessage("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferMoneyRequest);
    }

    @ParameterizedTest(name = "{displayName} {0}")
    @MethodSource("inValidAmountData")
    public void transferMoneywithInvalidAmountTest(String testName, Double amount, String error) {
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
        UserAccount secondUserAccount = new UserCreateAccountRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.entityCreated())
                .post().extract().response().as(UserAccount.class);

        int secondAccountId = secondUserAccount.getId();

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(5000).build();

        for (int i = 0; i < 3; i++) {
            new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                    .post(depositMoneyRequest);
        }

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(accountId)
                .receiverAccountId(secondAccountId)
                .amount(amount)
                .build();

        new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getBadReqStatusWithMessage(error))
                .post(transferMoneyRequest);

        List<UserAccount> userAccountswithTransfer = new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .response().jsonPath().getList("", UserAccount.class).stream()
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccountswithTransfer.get(0).getBalance(), 15000.0),
                () -> assertEquals(userAccountswithTransfer.get(1).getBalance(), 0.0)
        );
    }

    @Test
    public void transferMoneywithAmountMoreThanBalanceTest() {
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
        UserAccount secondUserAccount = new UserCreateAccountRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.entityCreated())
                .post().extract().response().as(UserAccount.class);

        int secondAccountId = secondUserAccount.getId();

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(5000).build();

        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(depositMoneyRequest);

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(accountId)
                .receiverAccountId(secondAccountId)
                .amount(7000)
                .build();

          new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass),
                  ResponseSpecs.getBadReqStatusWithMessage("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferMoneyRequest);
        List<UserAccount> userAccountswithTransfer = new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .response().jsonPath().getList("", UserAccount.class).stream()
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccountswithTransfer.get(0).getBalance(), 5000.0),
                () -> assertEquals(userAccountswithTransfer.get(1).getBalance(), 0.0)
        );
    }
}
