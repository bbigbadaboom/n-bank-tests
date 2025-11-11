package Common.Extensions;

import API.Models.CreateUserRequest;
import API.Models.UserAccount;
import API.skelethon.requesters.AdminSteps;
import API.skelethon.requesters.UserSteps;
import Common.Anotations.UserSession;
import Common.Anotations.WithAccounts;
import Common.Storage.AccountsStorage;
import Common.Storage.SessionStorage;
import UI.Pages.BasePage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.LinkedList;
import java.util.List;

import static Common.Common.generate;

public class UserExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        UserSession anotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
        WithAccounts accanotation = extensionContext.getRequiredTestMethod().getAnnotation(WithAccounts.class);
        if(anotation != null) {
          SessionStorage.clear();
            List<CreateUserRequest> users = new LinkedList<>();
            int count = anotation.value();
            for(int i = 0; i < count; i++) {
                CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
                AdminSteps.adminCreateUser(createUserRequest);
                users.add(createUserRequest);
            }
            SessionStorage.addUsers(users);
            int authAsUser = anotation.auth();
            BasePage.authUser(SessionStorage.getUser(authAsUser));
        }
        if (accanotation != null) {
            AccountsStorage.clear();
            List<CreateUserRequest> list = SessionStorage.getUsers();
            int count = accanotation.value();
            for (CreateUserRequest createUserRequest : list) {
                for (int i = 0; i < count; i++) {
                    UserAccount user = UserSteps.userCreateAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
                    AccountsStorage.addAccount(user, createUserRequest);
                }
            }
        }
    }
}
