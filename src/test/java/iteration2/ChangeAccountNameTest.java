package iteration2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import static Utils.Common.generate;
import static io.restassured.RestAssured.given;


public class ChangeAccountNameTest {
    private static Stream<Arguments> inValidData() {
        return Stream.of(
                Arguments.of("one word", "NewNAme", "Name must contain two words with letters only"),
                Arguments.of("using digits", "New1 Name", "Name must contain two words with letters only"),
                Arguments.of("using special charactrs", "New Name!", "Name must contain two words with letters only"),
                Arguments.of("Just spaces", "     ", "Name must contain two words with letters only")

        );
    }

    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );
    }

    @Test
    public void changeUsersNameTest() {
        String username = generate();
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
                        }""".replace("{userName}", username))
                .when()
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .header("Authorization");

        String nameAfterChange = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                          "name": "New Name"
                        }""")
                .when()
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response().jsonPath().get("customer.name").toString();
        assertEquals(nameAfterChange, "New Name");

        String nameInProdileRequest = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when()
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response().jsonPath().get("name").toString();
        assertEquals(nameInProdileRequest, "New Name");
    }

    @ParameterizedTest(name = "{displayName} {0}")
    @MethodSource("inValidData")
    public void changeUsersNameWithInvalidDataTest(String testName, String name, String error) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String username = generate();
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
                        }""".replace("{userName}", username))
                .when()
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .header("Authorization");

        String bodyError = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                          "name": "{name}"
                        }""".replace("{name}", name))
                .when()
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract()
                .response().getBody().asString();
        assertEquals(bodyError, error);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when()
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response();
        JsonNode node = mapper.readTree(response.getBody().asString());
        assertEquals(node.get("name").asText(), "null");
    }

}
