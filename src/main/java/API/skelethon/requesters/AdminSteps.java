package API.skelethon.requesters;

import API.Models.CreateUserRequest;
import API.Models.CreateUserResponse;
import API.Specs.RequestSpecs;
import API.Specs.ResponseSpecs;
import API.skelethon.EndPoints;

import java.util.List;

public class AdminSteps {


    public static CreateUserResponse adminCreateUser(CreateUserRequest createUserRequest) {
        return new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminAuthSpec(), EndPoints.POST_ADMIN_USER, ResponseSpecs.entityCreated())
                .post(createUserRequest);
    }
    public static String adminCreateUserWithMistake(CreateUserRequest createUserRequest) {
        return new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminAuthSpec(), EndPoints.POST_ADMIN_USER, ResponseSpecs.getBadReqStatus())
                .postErorr(createUserRequest);
    }

    public static List<CreateUserResponse> adminGetAllUsers() {
        return new ValidatedCrudRequester<CreateUserResponse>(RequestSpecs.adminAuthSpec(),
                EndPoints.GET_ADMIN_USER, ResponseSpecs.getOkStatus()).getList();

    }

    public static void adminCreateUserWithBadData(CreateUserRequest createUserRequest,String message) {
        new CrudRequester(RequestSpecs.adminAuthSpec(), EndPoints.POST_ADMIN_USER, ResponseSpecs.getBadReqStatusWithMessage(message))
                .post(createUserRequest);
    }
}
