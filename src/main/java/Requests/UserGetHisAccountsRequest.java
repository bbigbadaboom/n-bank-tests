package Requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class UserGetHisAccountsRequest extends Request {
    public UserGetHisAccountsRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .when()
                .get("api/v1/customer/accounts")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
