package skelethon;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class HttpRequester {


    public HttpRequester(RequestSpecification requestSpecification, EndPoints endPoint, ResponseSpecification responseSpecification) {
        this.requestSpecification = requestSpecification;
        this.endPoint = endPoint;
        this.responseSpecification = responseSpecification;
    }

    protected RequestSpecification requestSpecification;
    protected EndPoints endPoint;
    protected ResponseSpecification responseSpecification;
}
