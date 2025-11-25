package API.iteration2;

import API.BaseTest;
import API.Models.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import API.skelethon.Steps.AdminSteps;
import API.skelethon.Steps.UserSteps;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static Common.Common.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransferMoneyTest extends BaseTest {

    private static Stream<Arguments> inValidAmountData() {
        return Stream.of(
                Arguments.of("amount more than 10000", "10000.01", "Transfer amount cannot exceed 10000"),
                Arguments.of("amount 0", "0", "Transfer amount must be at least 0.01"),
                Arguments.of("amount -0.01", "-0.01", "Transfer amount must be at least 0.01")
        );
    }
    @Test
    public void transferMoneyBetweenUsersAccountsTest() {
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        long accountId = userAccount.getId();

        UserAccount secondUserAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());

        long secondAccountId = secondUserAccount.getId();

        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);

        UserSteps.userDepositMoney(depositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword());

        TransferMoneyRequest transferMoneyRequest = generate(TransferMoneyRequest .class);
        transferMoneyRequest.setSenderAccountId(accountId);
        transferMoneyRequest.setReceiverAccountId(secondAccountId);
        transferMoneyRequest.setAmount(amount);

        TransferMoneyResponse transferMoneyResponse = UserSteps.userTransferMoney(transferMoneyRequest,
                createUserRequest.getUsername(), createUserRequest.getPassword());

        assertAll(
                () -> assertEquals(transferMoneyResponse.getSenderAccountId(), accountId),
                () -> assertEquals(transferMoneyResponse.getMessage(), "Transfer successful"),
                () -> assertEquals(transferMoneyResponse.getAmount(), amount),
                () -> assertEquals(transferMoneyResponse.getReceiverAccountId(), secondAccountId)
        );
        List<UserAccount> userAccountswithTransfer =
                UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        List<UserAccount> userAccounts = userAccountswithTransfer.stream()
                .sorted(Comparator.comparingLong(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccounts.get(0).getBalance(), balance - amount),
                () -> assertEquals(userAccounts.get(1).getBalance(), amount)
        );
    }

    @Test
    public void transferMoneyBetweenDifferentUsersAccounts() {

        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);

        AdminSteps.adminCreateUser(createUserRequest);

        CreateUserRequest createSecondUserRequest = generate(CreateUserRequest.class);

        AdminSteps.adminCreateUser(createSecondUserRequest);

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        long accountId = userAccount.getId();

        UserAccount secondUserAccount = UserSteps.userCreateAccount(createSecondUserRequest.getUsername(), createSecondUserRequest.getPassword());;

        long secondAccountId = secondUserAccount.getId();

        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);

        UserSteps.userDepositMoney(depositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword());

        TransferMoneyRequest transferMoneyRequest = generate(TransferMoneyRequest .class);
        transferMoneyRequest.setSenderAccountId(accountId);
        transferMoneyRequest.setReceiverAccountId(secondAccountId);
        transferMoneyRequest.setAmount(amount);

        TransferMoneyResponse transferMoneyResponse = UserSteps.userTransferMoney(transferMoneyRequest,
                createUserRequest.getUsername(), createUserRequest.getPassword());

        assertAll(
                () -> assertEquals(transferMoneyResponse.getSenderAccountId(), accountId),
                () -> assertEquals(transferMoneyResponse.getMessage(), "Transfer successful"),
                () -> assertEquals(transferMoneyResponse.getAmount(), amount),
                () -> assertEquals(transferMoneyResponse.getReceiverAccountId(), secondAccountId)
        );

        List<UserAccount> firstUserAccountswithTransfer = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertEquals(firstUserAccountswithTransfer.get(0).getBalance(), balance - amount);

        List<UserAccount> secondUserAccountswithTransfer = UserSteps.userGetHisAccounts(createSecondUserRequest.getUsername(), createSecondUserRequest.getPassword());
        assertEquals(secondUserAccountswithTransfer.get(0).getBalance(), amount);
    }

    @Test
    public void transferMoneyFromInvalidAccountTest() {
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        long accountId = userAccount.getId();

        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);

        UserSteps.userDepositMoney(depositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword());

        TransferMoneyRequest transferMoneyRequest = generate(TransferMoneyRequest .class);
        transferMoneyRequest.setSenderAccountId(-10);
        transferMoneyRequest.setReceiverAccountId(accountId);
        transferMoneyRequest.setAmount(amount);
        UserSteps.userTransferMoneyWithNoAcces(transferMoneyRequest,createUserRequest.getUsername(), createUserRequest.getPassword());
    }

    @Test
    public void transferMoneyToInvalidAccountTest() {
        double balance = randomDouble(2000, 5001);
        double amount = randomDouble(1000, 2000);
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);

        AdminSteps.adminCreateUser(createUserRequest);

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        long accountId = userAccount.getId();

        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);

        UserSteps.userDepositMoney(depositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword());
        TransferMoneyRequest transferMoneyRequest = generate(TransferMoneyRequest .class);
        transferMoneyRequest.setSenderAccountId(accountId);
        transferMoneyRequest.setReceiverAccountId(-10);
        transferMoneyRequest.setAmount(amount);
        UserSteps.userTransferMoneyWitBadData(transferMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword(),
                "Invalid transfer: insufficient funds or invalid accounts");
    }

    @ParameterizedTest(name = "{displayName} {0}")
    @MethodSource("inValidAmountData")
    public void transferMoneywithInvalidAmountTest(String testName, Double amount, String error) {
        double balance = randomDouble(2000, 5001);
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        long accountId = userAccount.getId();
        UserAccount secondUserAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());

        long secondAccountId = secondUserAccount.getId();

        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);

        repeat(3, () -> UserSteps.userDepositMoney(depositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword()));

        TransferMoneyRequest transferMoneyRequest = generate(TransferMoneyRequest .class);
        transferMoneyRequest.setSenderAccountId(accountId);
        transferMoneyRequest.setReceiverAccountId(secondAccountId);
        transferMoneyRequest.setAmount(amount);

        UserSteps.userTransferMoneyWitBadData(transferMoneyRequest,createUserRequest.getUsername(), createUserRequest.getPassword(), error);

        List<UserAccount> userAccountswithTransfer = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        List<UserAccount> usersAccounts = userAccountswithTransfer.stream()
                .sorted(Comparator.comparingLong(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(usersAccounts.get(0).getBalance(), balance * 3),
                () -> assertEquals(usersAccounts.get(1).getBalance(), 0.0)
        );
    }

    @Test
    public void transferMoneywithAmountMoreThanBalanceTest() {
        double amount = randomDouble(2000, 5001);
        double balance = randomDouble(1000, 2000);
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);

        AdminSteps.adminCreateUser(createUserRequest);

        UserAccount userAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());

        long accountId = userAccount.getId();
        UserAccount secondUserAccount = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
        long secondAccountId = secondUserAccount.getId();

        DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
        depositMoneyRequest.setId(accountId);
        depositMoneyRequest.setBalance(balance);

        UserSteps.userDepositMoney(depositMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword());

        TransferMoneyRequest transferMoneyRequest = generate(TransferMoneyRequest .class);
        transferMoneyRequest.setSenderAccountId(accountId);
        transferMoneyRequest.setReceiverAccountId(secondAccountId);
        transferMoneyRequest.setAmount(amount);

        UserSteps.userTransferMoneyWitBadData(transferMoneyRequest, createUserRequest.getUsername(), createUserRequest.getPassword(),
                "Invalid transfer: insufficient funds or invalid accounts");
        List<UserAccount> userAccountswithTransfer = UserSteps.userGetHisAccounts(createUserRequest.getUsername(), createUserRequest.getPassword());
        List<UserAccount> userAccounts= userAccountswithTransfer.stream()
                .sorted(Comparator.comparingLong(UserAccount::getId))
                .toList();
        assertAll(
                () -> assertEquals(userAccounts.get(0).getBalance(), balance),
                () -> assertEquals(userAccounts.get(1).getBalance(), 0.0)
        );
    }
}
