package API.skelethon.Steps;

import API.Models.*;
import API.Specs.RequestSpecs;
import API.Specs.ResponseSpecs;
import API.skelethon.EndPoints;
import API.skelethon.requesters.CrudRequester;
import API.skelethon.requesters.ValidatedCrudRequester;
import Common.helpers.StepLogger;

import java.util.List;

public class UserSteps {
    private String name;
    private String pass;

    public UserSteps(String username, String password) {
        this.name = username;
        this.pass = password;
    }
    public static UserChangeNameResponse userChangeHisName(UserChangeNameRequest userChangeNameRequest, String name, String pass) {
        return StepLogger.log("User change name",  () -> {
            return new ValidatedCrudRequester<UserChangeNameResponse>
                    (RequestSpecs.userAuthSpec(name, pass), EndPoints.PUT_CUSTOMER_PROFILE,
                            ResponseSpecs.getOkStatus())
                    .put(userChangeNameRequest);
        });
    }

    public static void userLogin(LoginUserRequest loginUserRequest) {
         StepLogger.log("User login",  () -> {
            new CrudRequester(RequestSpecs.unAuthSpec(), EndPoints.POST_LOGIN, ResponseSpecs.getOkStatus())
                    .post(loginUserRequest);
        });
    }

    public static void userChangeHisNameWithBadData(UserChangeNameRequest userChangeNameRequest, String name, String pass, String error) {
        StepLogger.log("User change hos name with bad data",  () -> {
            new CrudRequester(RequestSpecs.userAuthSpec(name, pass),
                    EndPoints.PUT_CUSTOMER_PROFILE, ResponseSpecs.getBadReqStatusWithMessage(error))
                    .put(userChangeNameRequest);
        });

    }

    public static UserGetHisProfileResponse userGetHisProfile(String name, String pass) {
        return StepLogger.log("User get his profile",  () -> {
            return new ValidatedCrudRequester<UserGetHisProfileResponse>
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.GET_CUSTOMER_PROFILE, ResponseSpecs.getOkStatus())
                    .get();
        });
    }
    public UserGetHisProfileResponse userGetHisProfile() {
        return StepLogger.log("User get his profile",  () -> {
            return new ValidatedCrudRequester<UserGetHisProfileResponse>
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.GET_CUSTOMER_PROFILE, ResponseSpecs.getOkStatus())
                    .get();
        });
    }

    public static UserAccount userCreateAccount(String name, String pass) {
        return StepLogger.log("User create account",  () -> {
            return new ValidatedCrudRequester<UserAccount>
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.POST_ACCOUNTS, ResponseSpecs.entityCreated())
                    .post();
        });
    }

    public  UserAccount userCreateAccount() {
        return StepLogger.log("User create account",  () -> {
            return new ValidatedCrudRequester<UserAccount>
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.POST_ACCOUNTS, ResponseSpecs.entityCreated())
                    .post();
        });
    }

    public static DepositMoneyResponse userDepositMoney(DepositMoneyRequest depositMoneyRequest, String name, String pass) {
        return StepLogger.log("User deposit money",  () -> {
            return new ValidatedCrudRequester<DepositMoneyResponse>
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.POST_ACCOUNTS_DEPOSIT, ResponseSpecs.getOkStatus())
                    .post(depositMoneyRequest);
        });
    }

    public DepositMoneyResponse userDepositMoney(DepositMoneyRequest depositMoneyRequest) {
        return StepLogger.log("User deposit money",  () -> {
            return new ValidatedCrudRequester<DepositMoneyResponse>
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.POST_ACCOUNTS_DEPOSIT, ResponseSpecs.getOkStatus())
                    .post(depositMoneyRequest);
        });
    }

    public static void userDepositMoneyWithBadData(DepositMoneyRequest depositMoneyRequest, String name, String pass, String error) {
        StepLogger.log("User deposit money with bad data",  () -> {
            new CrudRequester(RequestSpecs.userAuthSpec(name, pass),
                    EndPoints.POST_ACCOUNTS_DEPOSIT, ResponseSpecs.getBadReqStatusWithMessage(error))
                    .post(depositMoneyRequest);
        });
    }

    public static void userDepositMoneyWithNoAcces(DepositMoneyRequest depositMoneyRequest, String name, String pass) {
        StepLogger.log("User deposit money with no acces",  () -> {
            new CrudRequester(RequestSpecs.userAuthSpec(name, pass),
                    EndPoints.POST_ACCOUNTS_DEPOSIT, ResponseSpecs.getForbiddenStatus())
                    .post(depositMoneyRequest);
        });
    }

    public List<UserAccount> userGetHisAccounts() {
        return StepLogger.log("User get his accounts",  () -> {
            return new ValidatedCrudRequester<UserAccount>
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.GET_CUSTOMER_ACCOUNTS, ResponseSpecs.getOkStatus())
                    .getList();
        });
    }

    public static List<UserAccount> userGetHisAccounts(String name, String pass) {
        return StepLogger.log("User get his accounts",  () -> {
            return new ValidatedCrudRequester<UserAccount>
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.GET_CUSTOMER_ACCOUNTS, ResponseSpecs.getOkStatus())
                    .getList();
        });
    }

    public static TransferMoneyResponse userTransferMoney(TransferMoneyRequest transferMoneyRequest, String name, String pass) {
        return StepLogger.log("User transfer money",  () -> {
            return new ValidatedCrudRequester<TransferMoneyResponse>
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.POST_ACCOUNTS_TRANSFER, ResponseSpecs.getOkStatus())
                    .post(transferMoneyRequest);
        });
    }

    public static void userTransferMoneyWithNoAcces(TransferMoneyRequest transferMoneyRequest, String name, String pass) {
        StepLogger.log("User transfer money with no acces",  () -> {
            new CrudRequester
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.POST_ACCOUNTS_TRANSFER, ResponseSpecs.getForbiddenStatus())
                    .post(transferMoneyRequest);
        });
    }

    public static void userTransferMoneyWitBadData(TransferMoneyRequest transferMoneyRequest, String name, String pass, String error) {
        StepLogger.log("User transfer money with bad data",  () -> {
            new CrudRequester
                    (RequestSpecs.userAuthSpec(name, pass),
                            EndPoints.POST_ACCOUNTS_TRANSFER, ResponseSpecs.getBadReqStatusWithMessage(error))
                    .post(transferMoneyRequest);
        });
    }

    public TransferResponse transferWithFraudCheck(Long senderAccountId, Long receiverAccountId, double amount) {
        return StepLogger.log("User transfer money with fraud check",  () -> {
            TransferRequest transferRequest = TransferRequest.builder()
                    .senderAccountId(senderAccountId)
                    .receiverAccountId(receiverAccountId)
                    .amount(amount)
                    .description("Test transfer with fraud check")
                    .build();

            return new ValidatedCrudRequester<TransferResponse>(
                    RequestSpecs.userAuthSpec(name, pass),
                    EndPoints.TRANSFER_WITH_FRAUD_CHECK,
                    ResponseSpecs.getOkStatus()).post(transferRequest);
        });
    }

}
