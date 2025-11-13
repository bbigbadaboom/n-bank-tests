package Common.Storage;

import API.Models.CreateUserRequest;
import API.Models.UserAccount;

import java.util.*;

public class AccountsStorage {
    private static final ThreadLocal<AccountsStorage> INTSTANCE = ThreadLocal.withInitial(AccountsStorage::new);
    private final LinkedHashMap<UserAccount, CreateUserRequest> userAccounts = new LinkedHashMap<>();
    private AccountsStorage(){
    }
    public static void addAccount(UserAccount userAccount, CreateUserRequest createUserRequest) {
            INTSTANCE.get().userAccounts.put(userAccount, createUserRequest);
        }

    public static List<UserAccount> getAccounts() {
        return new ArrayList<>(INTSTANCE.get().userAccounts.keySet());
    }

    public static void clear() {
        INTSTANCE.get().userAccounts.clear();
    }
}
