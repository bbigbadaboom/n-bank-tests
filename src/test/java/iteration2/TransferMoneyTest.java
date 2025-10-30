package iteration2;

import Models.*;
import Requests.*;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static Common.Common.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransferMoneyTest {

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
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);
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

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(balance).build();

        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(depositMoneyRequest);

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(accountId)
                .receiverAccountId(secondAccountId)
                .amount(amount)
                .build();

        TransferMoneyResponse transferMoneyResponse = new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(transferMoneyRequest)
                .extract()
                .response().as(TransferMoneyResponse.class);

        assertAll(
                () -> assertEquals(transferMoneyResponse.getSenderAccountId(), accountId),
                () -> assertEquals(transferMoneyResponse.getMessage(), "Transfer successful"),
                () -> assertEquals(transferMoneyResponse.getAmount(), amount),
                () -> assertEquals(transferMoneyResponse.getReceiverAccountId(), secondAccountId)
        );
        List<UserAccount> userAccountswithTransfer =
                Arrays.stream((UserAccount[])new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .get()
                .extract().as(UserAccount.class.arrayType()))
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccountswithTransfer.get(0).getBalance(), balance - amount),
                () -> assertEquals(userAccountswithTransfer.get(1).getBalance(), amount)
        );
    }

    @Test
    public void transferMoneyBetweenDifferentUsersAccounts() {
        String name = generateName();
        String pass = generatePassword(10);
        String secondName = generateName();
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);
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

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(balance).build();

            new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                    .post(depositMoneyRequest);

            TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(accountId)
                .receiverAccountId(secondAccountId)
                .amount(amount)
                .build();

        TransferMoneyResponse transferMoneyResponse = new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(transferMoneyRequest)
                .extract()
                .response().as(TransferMoneyResponse.class);

        assertAll(
                () -> assertEquals(transferMoneyResponse.getSenderAccountId(), accountId),
                () -> assertEquals(transferMoneyResponse.getMessage(), "Transfer successful"),
                () -> assertEquals(transferMoneyResponse.getAmount(), amount),
                () -> assertEquals(transferMoneyResponse.getReceiverAccountId(), secondAccountId)
        );

        List<UserAccount> firstUserAccountswithTransfer = Arrays.asList((UserAccount[])new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass),
                ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .as(UserAccount.class.arrayType()));
        assertEquals(firstUserAccountswithTransfer.get(0).getBalance(), balance - amount);

        List<UserAccount> secondUserAccountswithTransfer = Arrays.asList((UserAccount[])new UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(secondName, pass),
                ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .as(UserAccount.class.arrayType()));
        assertEquals(secondUserAccountswithTransfer.get(0).getBalance(), amount);
    }

    @Test
    public void transferMoneyFromInvalidAccountTest() {
        String name = generateName();
        String pass = generatePassword(10);
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);
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

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(balance).build();

        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(depositMoneyRequest);

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(-10)
                .receiverAccountId(accountId)
                .amount(amount)
                .build();

        new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getForbiddenStatus())
                .post(transferMoneyRequest);
    }

    @Test
    public void transferMoneyToInvalidAccountTest() {
        String name = generateName();
        String pass = generatePassword(10);
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);
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

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(balance).build();

        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(depositMoneyRequest);
        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(accountId)
                .receiverAccountId(-10)
                .amount(amount)
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
        double balance = randomDouble(2000, 5001);
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

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(balance).build();


        repeat(3, () -> new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                    .post(depositMoneyRequest));

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(accountId)
                .receiverAccountId(secondAccountId)
                .amount(amount)
                .build();

        new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getBadReqStatusWithMessage(error))
                .post(transferMoneyRequest);

        List<UserAccount> userAccountswithTransfer = Arrays.stream((UserAccount[])new
                        UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .get()
                .extract().as(UserAccount.class.arrayType()))
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccountswithTransfer.get(0).getBalance(), balance * 3),
                () -> assertEquals(userAccountswithTransfer.get(1).getBalance(), 0.0)
        );
    }

    @Test
    public void transferMoneywithAmountMoreThanBalanceTest() {
        String name = generateName();
        String pass = generatePassword(10);
        double amount = randomDouble(2000, 5001);
        double balance = randomDouble(1000, 2000);
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

        DepositMoneyRequest depositMoneyRequest = DepositMoneyRequest.builder().id(accountId).balance(balance).build();

        new UserDepositMoneyRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .post(depositMoneyRequest);

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest
                .builder()
                .senderAccountId(accountId)
                .receiverAccountId(secondAccountId)
                .amount(amount)
                .build();

          new UserTransferMoneyRequest(RequestSpecs.userAuthSpec(name, pass),
                  ResponseSpecs.getBadReqStatusWithMessage("Invalid transfer: insufficient funds or invalid accounts"))
                .post(transferMoneyRequest);
        List<UserAccount> userAccountswithTransfer = Arrays.stream((UserAccount[])new
                        UserGetHisAccountsRequest(RequestSpecs.userAuthSpec(name, pass), ResponseSpecs.getOkStatus())
                .get()
                .extract().as(UserAccount.class.arrayType()))
                .sorted(Comparator.comparingInt(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccountswithTransfer.get(0).getBalance(), balance),
                () -> assertEquals(userAccountswithTransfer.get(1).getBalance(), 0.0)
        );
    }
}
