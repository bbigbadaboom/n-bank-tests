package Specs;

import Models.CreateUserRequest;
import Models.Roles;
import Requests.AdminCreateUserRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class RequestSpecs {
    private RequestSpecs(){
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()))
                .setBaseUri("http://localhost:4111");
    }
    public static RequestSpecification unAuthSpec(){
        return defaultRequestBuilder().build();
    }
    public static RequestSpecification adminAuthSpec(){
        return defaultRequestBuilder().addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
        .build();
    }

    public static RequestSpecification userAuthSpec(String auth){
        return defaultRequestBuilder().addHeader("Authorization", auth)
                .build();
    }
}
