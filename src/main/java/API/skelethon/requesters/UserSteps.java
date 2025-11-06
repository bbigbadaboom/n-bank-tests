package API.skelethon.requesters;

import API.Models.*;
import API.Specs.RequestSpecs;
import API.Specs.ResponseSpecs;
import API.skelethon.EndPoints;

import java.util.List;

public class UserSteps {
    public static UserChangeNameResponse userChangeHisName(UserChangeNameRequest userChangeNameRequest, String name, String pass) {
        return new ValidatedCrudRequester<UserChangeNameResponse>
                (RequestSpecs.userAuthSpec(name, pass), EndPoints.PUT_CUSTOMER_PROFILE,
                        ResponseSpecs.getOkStatus())
                .put(userChangeNameRequest);

    }

    public static void userLogin(LoginUserRequest loginUserRequest) {
        new CrudRequester(RequestSpecs.adminAuthSpec(), EndPoints.POST_LOGIN, ResponseSpecs.getOkStatus())
                .post(loginUserRequest);

    }

    public static void userChangeHisNameWithBadData(UserChangeNameRequest userChangeNameRequest, String name, String pass, String error) {
        new CrudRequester(RequestSpecs.userAuthSpec(name, pass),
                EndPoints.PUT_CUSTOMER_PROFILE, ResponseSpecs.getBadReqStatusWithMessage(error))
                .put(userChangeNameRequest);

    }

    public static UserGetHisProfileResponse userGetHisProfile(String name, String pass) {
        return  new ValidatedCrudRequester<UserGetHisProfileResponse>
                (RequestSpecs.userAuthSpec(name, pass),
                        EndPoints.GET_CUSTOMER_PROFILE, ResponseSpecs.getOkStatus())
                .get();
    }

    public static UserAccount userCreateAccount(String name, String pass) {
        return new ValidatedCrudRequester<UserAccount>
                (RequestSpecs.userAuthSpec(name, pass),
                        EndPoints.POST_ACCOUNTS, ResponseSpecs.entityCreated())
                .post();

    }

    public static DepositMoneyResponse userDepositMoney(DepositMoneyRequest depositMoneyRequest, String name, String pass) {
        return new ValidatedCrudRequester<DepositMoneyResponse>
                (RequestSpecs.userAuthSpec(name, pass),
                        EndPoints.POST_ACCOUNTS_DEPOSIT, ResponseSpecs.getOkStatus())
                .post(depositMoneyRequest);
    }

    public static void userDepositMoneyWithBadData(DepositMoneyRequest depositMoneyRequest, String name, String pass, String error) {
        new CrudRequester(RequestSpecs.userAuthSpec(name, pass),
                EndPoints.POST_ACCOUNTS_DEPOSIT, ResponseSpecs.getBadReqStatusWithMessage(error))
                .post(depositMoneyRequest);
    }

    public static void userDepositMoneyWithNoAcces(DepositMoneyRequest depositMoneyRequest, String name, String pass) {
        new CrudRequester(RequestSpecs.userAuthSpec(name, pass),
                EndPoints.POST_ACCOUNTS_DEPOSIT, ResponseSpecs.getForbiddenStatus())
                .post(depositMoneyRequest);
    }

    public static List<UserAccount> userGetHisAccounts(String name, String pass) {
        return new ValidatedCrudRequester<UserAccount>
                (RequestSpecs.userAuthSpec(name, pass),
                        EndPoints.GET_CUSTOMER_ACCOUNTS, ResponseSpecs.getOkStatus())
                .getList();
    }

    public static TransferMoneyResponse userTransferMoney(TransferMoneyRequest transferMoneyRequest, String name, String pass) {
        return new ValidatedCrudRequester<TransferMoneyResponse>
                (RequestSpecs.userAuthSpec(name, pass),
                        EndPoints.POST_ACCOUNTS_TRANSFER, ResponseSpecs.getOkStatus())
                .post(transferMoneyRequest);
    }

    public static void userTransferMoneyWithNoAcces(TransferMoneyRequest transferMoneyRequest, String name, String pass) {
         new CrudRequester
                (RequestSpecs.userAuthSpec(name, pass),
                        EndPoints.POST_ACCOUNTS_TRANSFER, ResponseSpecs.getForbiddenStatus())
                .post(transferMoneyRequest);
    }

    public static void userTransferMoneyWitBadData(TransferMoneyRequest transferMoneyRequest, String name, String pass, String error) {
        new CrudRequester
                (RequestSpecs.userAuthSpec(name, pass),
                        EndPoints.POST_ACCOUNTS_TRANSFER, ResponseSpecs.getBadReqStatusWithMessage(error))
                .post(transferMoneyRequest);
    }

}
