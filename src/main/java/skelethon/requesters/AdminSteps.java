package skelethon.requesters;

import Models.BaseModel;
import Models.CreateUserRequest;
import Models.CreateUserResponse;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import skelethon.EndPoints;

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
