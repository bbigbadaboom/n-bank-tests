package iteration1;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import static Utils.Common.generate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class CreateUserTest {
    private static Stream<Arguments> invalidData() {
        return Stream.of(
                Arguments.of("blank username", "     ",  "Kate1998@", "USER",
                        "{\"username\":[\"Username must contain only letters, digits, dashes, underscores, and dots\"," +
                                "\"Username cannot be blank\"]}"),
                Arguments.of("2 letters username", "ka", "Kate1998@", "USER", """
                        {"username":["Username must be between 3 and 15 characters"]}"""),
                Arguments.of("16 letters username", "Ka123456789.-_ss", "Kate1998@", "USER", """
                        {"username":["Username must be between 3 and 15 characters"]}"""),
                Arguments.of("invalid symbol in username", "Ka1236789.-_ss@", "Kate1998@", "USER", """
                        {"username":["Username must contain only letters, digits, dashes, underscores, and dots"]}"""),
                Arguments.of("space in username", "Ka123678 .-_ss", "Kate1998@", "USER", """
                        {"username":["Username must contain only letters, digits, dashes, underscores, and dots"]}"""),
                Arguments.of("empty password", "Ka123678.-_ss", "        ", "USER", """
                        {"password":["Password cannot be blank","Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"]}"""),
                Arguments.of("password without special symbol", "Ka123678.-_ss", "Kate1988", "USER", """
                        {"password":["Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"]}"""),
                Arguments.of("7 characters password", "Ka123678.-_ss", "Kate12!", "USER", """
                        {"password":["Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"]}"""),
                Arguments.of("password without digits", "Ka123678.-_ss", "Katesada!", "USER", """
                        {"password":["Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"]}"""),
                Arguments.of("password without uppercase", "Ka123678.-_ss", "katesa12da!", "USER", """
                        {"password":["Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"]}"""),
                Arguments.of("password without lowercase", "Ka123678.-_ss", "KATE1234!", "USER", """
                        {"password":["Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"]}"""),
                Arguments.of("password without lowercase", "Ka123678.-_ss", "KAte1234!", "EDITOR", """
                        {"role":["Role must be either 'ADMIN' or 'USER'"]}""")

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
    public void adminCanCreateUserWithCorrectDataTest() {
        String name = generate();

        given()
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
                .body("username", Matchers.equalTo(name))
                .body("password", Matchers.not(Matchers.equalTo("Kate2000!")))
                .body("role", Matchers.equalTo("USER"));

        List<String> usernames = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .when()
                .get("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                 .extract().response().jsonPath().getList("username");
        assertTrue(usernames.contains(name));
    }

    @ParameterizedTest(name="{displayName} {0}")
    @MethodSource("invalidData")
    public void adminCantCreateUserWithInvalidDataTest(String name, String username, String password, String role, String responseBody) {
        String requestBody =String.format("""
                        {
                          "username": "%s",
                          "password": "%s"   ,
                          "role": "%s"
                        }""", username, password, role );

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(requestBody)
                .when()
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo(responseBody));
    }

    @Test
    public void adminCanCreateUserWithCorrectDataButWithExistedUsernameTest() {
        String name = generate();
        String errorMessage;

        given()
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
                .statusCode(HttpStatus.SC_CREATED);

        errorMessage = given()
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
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().body().asString();
        assertEquals(errorMessage, "Error: Username '" + name + "' already exists.");
    }

}
