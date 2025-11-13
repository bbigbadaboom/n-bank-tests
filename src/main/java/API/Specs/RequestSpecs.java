package API.Specs;

import API.Configs.Config;
import API.Models.CreateUserRequest;
import API.Models.LoginUserRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import API.skelethon.EndPoints;
import API.skelethon.requesters.CrudRequester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecs {
    private RequestSpecs(){
    }
    private static Map<String, String> tokens = new HashMap<>(Map.of("admin", "Basic YWRtaW46YWRtaW4="));

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                //.addFilters(List.of(new RequestLoggingFilter(),
             //           new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperties("server") + Config.getProperties("apiVersion"));
    }
    public static RequestSpecification unAuthSpec(){
        return defaultRequestBuilder().build();
    }
    public static RequestSpecification adminAuthSpec(){
        return defaultRequestBuilder().addHeader("Authorization", tokens.get("admin"))
        .build();
    }

    public static RequestSpecification userAuthSpec(String name, String pass){
        return defaultRequestBuilder().addHeader("Authorization", getAuth(name, pass))
                .build();
    }

    public static String getAuth(String name, String pass) {
        if(!tokens.containsKey(name)) {
            LoginUserRequest loginUserRequest = LoginUserRequest
                    .builder()
                    .username(name)
                    .password(pass)
                    .build();
            String token = new CrudRequester(RequestSpecs.unAuthSpec(), EndPoints.POST_LOGIN, ResponseSpecs.getOkStatus())
                    .post(loginUserRequest)
                    .extract()
                    .header("Authorization");
            tokens.put(name, token);
        }
        return tokens.get(name);

    }
}
