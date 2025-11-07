package Common.Extensions;

import API.Configs.Config;
import API.Models.LoginUserRequest;
import Common.Anotations.AdminSession;
import UI.Pages.BasePage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static API.Common.Common.generate;

public class AdminSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        AdminSession anotation = extensionContext.getRequiredTestMethod().getAnnotation(AdminSession.class);
        if(anotation != null) {
            LoginUserRequest loginUserRequest = generate(LoginUserRequest.class);
            loginUserRequest.setUsername(API.Configs.Config.getProperties("admin.name"));
            loginUserRequest.setPassword(Config.getProperties("admin.pass"));
            BasePage.authUser(loginUserRequest);
        }
    }
}
