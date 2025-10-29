package Requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class UserCreateAccountRequest extends Request {
    public UserCreateAccountRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse post() {
        return given()
                .spec(requestSpecification)
                .when()
                .post("/api/v1/accounts")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
