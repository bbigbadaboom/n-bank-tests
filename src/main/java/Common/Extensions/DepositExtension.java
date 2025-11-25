package Common.Extensions;

import API.Models.CreateUserRequest;
import API.Models.DepositMoneyRequest;
import API.Models.TestType;
import API.Models.UserAccount;
import API.skelethon.Steps.AdminSteps;
import API.skelethon.Steps.UserSteps;
import Common.Anotations.UserSession;
import Common.Anotations.UserWithAccountsAndDeposit;
import Common.Anotations.WithAccounts;
import Common.Storage.AccountsStorage;
import Common.Storage.SessionStorage;
import UI.Pages.BasePage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.LinkedList;
import java.util.List;

import static Common.Common.generate;
import static Common.Common.randomDouble;

public class DepositExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
       UserWithAccountsAndDeposit anotation = extensionContext.getRequiredTestMethod().getAnnotation(UserWithAccountsAndDeposit.class);
        if(anotation != null) {
          SessionStorage.clear();
            List<CreateUserRequest> users = new LinkedList<>();
                CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
                AdminSteps.adminCreateUser(createUserRequest);
                users.add(createUserRequest);

            SessionStorage.addUsers(users);
        }
            AccountsStorage.clear();
            List<CreateUserRequest> list = SessionStorage.getUsers();
            int count = anotation.accounts();
            for (CreateUserRequest createUserRequest : list) {
                for (int i = 0; i < count; i++) {
                    UserAccount user = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
                    AccountsStorage.addAccount(user, createUserRequest);
                }
                double balance = randomDouble(2000, 5001);
                int accNumber = anotation.accFordep();
                long accountId = AccountsStorage.getAccounts().get(accNumber - 1).getId();
                DepositMoneyRequest depositMoneyRequest = generate(DepositMoneyRequest.class);
                depositMoneyRequest.setId(accountId);
                depositMoneyRequest.setBalance(balance);
                SessionStorage.getSteps().userDepositMoney(depositMoneyRequest);
            }
        }
    }

