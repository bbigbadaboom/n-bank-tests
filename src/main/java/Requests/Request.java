package Requests;

import Models.BaseModel;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class Request {
    public Request(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        this.requestSpecification = requestSpecification;
        this.responseSpecification = responseSpecification;
    }

    protected RequestSpecification requestSpecification;
    protected ResponseSpecification responseSpecification;

    public ValidatableResponse post(BaseModel baseModel) {
        return null;
    }

    public ValidatableResponse get() {
        return null;
    }

    public ValidatableResponse put(BaseModel baseModel) {
        return null;
    }

}
