package API.skelethon;

import API.Models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum EndPoints {

    POST_ADMIN_USER(
            "admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),
    GET_ADMIN_USER(
            "admin/users",
            BaseModel.class,
            CreateUserResponse.class
    ),

    POST_LOGIN(
            "auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),
    POST_ACCOUNTS(
            "accounts",
            BaseModel.class,
            UserAccount.class
    ),

    GET_CUSTOMER_ACCOUNTS(
            "customer/accounts",
            BaseModel.class,
            UserAccount.class
    ),
    PUT_CUSTOMER_PROFILE(
            "customer/profile",
            UserChangeNameRequest.class,
            UserChangeNameResponse.class
    ),
    GET_CUSTOMER_PROFILE(
            "customer/profile",
            BaseModel.class,
            UserGetHisProfileResponse.class
    ),
    POST_ACCOUNTS_DEPOSIT(
            "accounts/deposit",
            DepositMoneyRequest.class,
            DepositMoneyResponse.class
    ),
    POST_ACCOUNTS_TRANSFER(
            "accounts/transfer",
            TransferMoneyRequest.class,
            TransferMoneyResponse.class
    ),
    TRANSFER_WITH_FRAUD_CHECK(
            "/accounts/transfer-with-fraud-check",
            TransferRequest.class,
            TransferResponse.class
    ),

    FRAUD_CHECK_STATUS(
            "/api/v1/accounts/fraud-check/{transactionId}",
            BaseModel.class,
            FraudCheckResponse.class
    );


    public final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
