package skelethon.requesters;

import Models.BaseModel;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import skelethon.EndPoints;
import skelethon.HttpRequester;
import skelethon.interfaces.CrudInterface;
import skelethon.interfaces.SpecialCruds;
import skelethon.interfaces.SpecialExtracts;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequester implements CrudInterface, SpecialCruds {
    public CrudRequester(RequestSpecification requestSpecification, EndPoints endPoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endPoint, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel baseModel) {

        return given()
                .spec(requestSpecification)
                .body(baseModel)
                .when()
                .post(endPoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .when()
                .get(endPoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put(BaseModel baseModel) {
        return given()
                .spec(requestSpecification)
                .body(baseModel)
                .when()
                .put(endPoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public Object delete(long id) {
        return null;
    }

    @Override
    public ValidatableResponse post() {
        return given()
                .spec(requestSpecification)
                .when()
                .post(endPoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
