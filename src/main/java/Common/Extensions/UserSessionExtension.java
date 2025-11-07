package Common.Extensions;

import API.Common.Common;
import API.Models.CreateUserRequest;
import API.Models.CreateUserResponse;
import API.skelethon.requesters.AdminSteps;
import Common.Anotations.UserSession;
import Common.Storage.SessionStorage;
import UI.Pages.BasePage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.LinkedList;
import java.util.List;

import static API.Common.Common.generate;

public class UserSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        UserSession anotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
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
    }
}
