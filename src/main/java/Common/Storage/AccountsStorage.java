package Common.Storage;

import API.Models.CreateUserRequest;
import API.Models.UserAccount;
import API.skelethon.requesters.UserSteps;

import java.util.*;

public class AccountsStorage {
    private static final AccountsStorage INTSTANCE = new AccountsStorage();
    private final LinkedHashMap<UserAccount, CreateUserRequest> userAccounts = new LinkedHashMap<>();
    private AccountsStorage(){
    }
    public static void addAccount(UserAccount userAccount, CreateUserRequest createUserRequest) {
            INTSTANCE.userAccounts.put(userAccount, createUserRequest);
        }

    public static List<UserAccount> getAccounts() {
        return new ArrayList<>(INTSTANCE.userAccounts.keySet());
    }
}
