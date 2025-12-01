package API.skelethon.Steps;

import API.Models.CreateUserRequest;
import API.Models.CreateUserResponse;
import API.Specs.RequestSpecs;
import API.Specs.ResponseSpecs;
import API.skelethon.EndPoints;
import API.skelethon.requesters.CrudRequester;
import API.skelethon.requesters.ValidatedCrudRequester;
import Common.helpers.StepLogger;

import java.util.List;

public class AdminSteps {


    public static CreateUserResponse adminCreateUser(CreateUserRequest createUserRequest) {
        return StepLogger.log("Admin create user",  () -> {
            return new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminAuthSpec(), EndPoints.POST_ADMIN_USER, ResponseSpecs.entityCreated())
                    .post(createUserRequest);
        });
    }
    public static String adminCreateUserWithMistake(CreateUserRequest createUserRequest) {
        return StepLogger.log("Admin create user with mistake",  () -> {
            return new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminAuthSpec(), EndPoints.POST_ADMIN_USER, ResponseSpecs.getBadReqStatus())
                    .postErorr(createUserRequest);
        });
    }

    public static List<CreateUserResponse> adminGetAllUsers() {
        return StepLogger.log("Admin get all users",  () -> {
            return new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminAuthSpec(),
                    EndPoints.GET_ADMIN_USER, ResponseSpecs.getOkStatus()).getList();
        });

    }

    public static void adminCreateUserWithBadData(CreateUserRequest createUserRequest, String key, List<String> message) {
          StepLogger.log("Admin create user with mistake",  () -> {
            new CrudRequester(RequestSpecs.adminAuthSpec(), EndPoints.POST_ADMIN_USER, ResponseSpecs.getBadReqStatusWithMessage(key, message))
                    .post(createUserRequest);
        });
    }
}
