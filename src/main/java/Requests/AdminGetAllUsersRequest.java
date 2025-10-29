package Requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class AdminGetAllUsersRequest extends Request {
    public AdminGetAllUsersRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/v1/admin/users")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
