package iteration1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static Utils.Common.generate;
import static io.restassured.RestAssured.given;

public class CreateAccountTest {

    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );
    }

    @Test
    public void createUserAccountTest() throws JsonProcessingException {
        String name = generate();
        ObjectMapper objectMapper = new ObjectMapper();
        String auth;
        auth = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "{userName}",
                          "password": "Kate2000!"   ,
                          "role": "USER"
                        }""".replace("{userName}", name))
                .when()
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .header("Authorization");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);

        JsonNode body = objectMapper.readTree(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when()
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response().
                getBody().asString());
        assertEquals(body.get(0).get("balance").toString(), "0.0");
        assertTrue(body.get(0).get("transactions").isEmpty());
    }
}


