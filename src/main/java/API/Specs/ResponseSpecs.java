package API.Specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.List;

public class ResponseSpecs {
    private ResponseSpecs(){
    }

    private static ResponseSpecBuilder defaaultSpecBuilder() {
        return new ResponseSpecBuilder();
    }
    public static ResponseSpecification entityCreated() {
        return defaaultSpecBuilder().expectStatusCode(HttpStatus.SC_CREATED).build();
    }
    public static ResponseSpecification getOkStatus() {
        return defaaultSpecBuilder().expectStatusCode(HttpStatus.SC_OK).build();
    }

    public static ResponseSpecification getOkStatusAndCheckHeader(String headerName, String header) {
        return defaaultSpecBuilder().expectStatusCode(HttpStatus.SC_OK).expectHeader(headerName, header).build();
    }

    public static ResponseSpecification getBadReqStatus() {
        return defaaultSpecBuilder().expectStatusCode(HttpStatus.SC_BAD_REQUEST).build();
    }
    public static ResponseSpecification getForbiddenStatus() {
        return defaaultSpecBuilder().expectStatusCode(HttpStatus.SC_FORBIDDEN).build();
    }

    public static ResponseSpecification getBadReqStatusWithMessage(String errorKey, List<String> error) {
        return defaaultSpecBuilder().expectStatusCode(HttpStatus.SC_BAD_REQUEST).expectBody(errorKey, Matchers.containsInAnyOrder(error.toArray())).build();
    }
    public static ResponseSpecification getBadReqStatusWithMessage(String error) {
        return defaaultSpecBuilder().expectStatusCode(HttpStatus.SC_BAD_REQUEST).expectBody(Matchers.equalTo(error)).build();
    }

}
