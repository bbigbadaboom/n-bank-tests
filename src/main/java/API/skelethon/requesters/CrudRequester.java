package API.skelethon.requesters;

import API.Configs.Config;
import API.Models.BaseModel;
import API.skelethon.interfaces.CrudInterface;
import API.skelethon.interfaces.SpecialCruds;
import Common.helpers.StepLogger;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import API.skelethon.EndPoints;
import API.skelethon.HttpRequester;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequester implements CrudInterface, SpecialCruds {
    private final String APIVERSION = Config.getProperties("apiVersion");
    public CrudRequester(RequestSpecification requestSpecification, EndPoints endPoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endPoint, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel baseModel) {
        return StepLogger.log("POST request to " + endPoint.getUrl(),  () -> {
            return given()
                    .spec(requestSpecification)
                    .body(baseModel)
                    .when()
                    .post(APIVERSION + endPoint.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    @Override
    public ValidatableResponse get() {
        return StepLogger.log("GET request to " + endPoint.getUrl(),  () -> {
            return given()
                    .spec(requestSpecification)
                    .when()
                    .get(APIVERSION + endPoint.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    @Override
    public ValidatableResponse put(BaseModel baseModel) {
        return StepLogger.log("PUT request to " + endPoint.getUrl(),  () -> {
            return given()
                    .spec(requestSpecification)
                    .body(baseModel)
                    .when()
                    .put(APIVERSION + endPoint.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    @Override
    public Object delete(long id) {
        return null;
    }

    @Override
    public ValidatableResponse post() {
        return StepLogger.log("POST request to " + endPoint.getUrl(),  () -> {
            return given()
                    .spec(requestSpecification)
                    .when()
                    .post(APIVERSION + endPoint.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecification);


        });
    }

}
