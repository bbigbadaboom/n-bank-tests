package skelethon;

import Models.*;
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
    );


    public final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
