package iteration1;

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

import java.util.List;
import java.util.stream.Stream;

import static Utils.Common.generate;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepositMoneyTest {
    private static Stream<Arguments> validData() {
        return Stream.of(
                Arguments.of("0.01 deposit", "0.01"),
                Arguments.of("5000 deposit", "5000"),
                Arguments.of("4999.99 deposit", "4999.99")

        );
    }
    private static Stream<Arguments> inValidBalanceData() {
        return Stream.of(
                Arguments.of("balance more than 5000", "5000.01", "Deposit amount cannot exceed 5000"),
                Arguments.of("balance 0", "0", "Deposit amount must be at least 0.01"),
                Arguments.of("balance -0.01", "-0.01", "Deposit amount must be at least 0.01"),
                Arguments.of("balance 5001", "5001", "Deposit amount cannot exceed 5000")
        );
    }
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );
    }

    @ParameterizedTest(name="{displayName} {0}")
    @MethodSource("validData")
    public void userDepositMoneyWithValidDataTest(String testName, String amount) throws JsonProcessingException {
        String name = generate();
        ObjectMapper objectMapper = new ObjectMapper();
        String auth;
        String accountId;
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
                        .response()
                .header("Authorization");

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        JsonNode body = objectMapper.readTree(response.getBody().asString());
        accountId = body.get("id").asText();

        JsonNode nodeForDeposit = objectMapper.readTree(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                           "id": {id},
                           "balance": {balance} 
                         }""".replace("{id}", accountId).replace("{balance}", amount))
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK).extract()
                .response().
                getBody().asString());
        assertEquals(nodeForDeposit.get("id").toString(), accountId);
        assertEquals(Double.parseDouble(nodeForDeposit.get("balance").toString()), Double.parseDouble(amount));
        assertEquals(nodeForDeposit.get("transactions").get(0).get("type").asText(), "DEPOSIT");
        assertEquals(Double.parseDouble(nodeForDeposit.get("transactions").get(0).get("amount").asText()), Double.parseDouble(amount));

        JsonNode nodeForGetAccounts = objectMapper.readTree(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when(  )
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response().
                getBody().asString());
        assertEquals(nodeForGetAccounts.get(0).get("id").toString(), accountId);
        assertEquals(Double.parseDouble(nodeForGetAccounts.get(0).get("balance").toString()), Double.parseDouble(amount));
        assertEquals(nodeForGetAccounts.get(0).get("transactions").size(), 1);
    }

    @Test
    public void userDepositMoneyWithValidDatawith2DepositsTest() throws JsonProcessingException {
        String name = generate();
        ObjectMapper objectMapper = new ObjectMapper();
        String auth;
        String accountId;
        String firstAmount = "2000";
        String secondAmount = "3000";
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
                .response()
                .header("Authorization");

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        JsonNode body = objectMapper.readTree(response.getBody().asString());
        accountId = body.get("id").asText();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                           "id": {id},
                           "balance": {balance} 
                         }""".replace("{id}", accountId).replace("{balance}", firstAmount))
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                           "id": {id},
                           "balance": {balance} 
                         }""".replace("{id}", accountId).replace("{balance}", secondAmount))
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        JsonNode nodeForGetAccounts = objectMapper.readTree(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when(  )
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response().
                getBody().asString());
        assertEquals(nodeForGetAccounts.get(0).get("id").toString(), accountId);
        assertEquals(Double.parseDouble(nodeForGetAccounts.get(0).get("balance").toString()),
                Double.parseDouble(firstAmount) + Double.parseDouble(secondAmount));
        assertEquals(nodeForGetAccounts.get(0).get("transactions").size(), 2);
    }

    @ParameterizedTest(name="{displayName} {0}")
    @MethodSource("inValidBalanceData")
    public void userDepositMoneyWithinValidBalanceDataTest(String testName, String amount, String error) throws JsonProcessingException {
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
                .response()
                .header("Authorization");

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        JsonNode body = objectMapper.readTree(response.getBody().asString());
        String accountId = body.get("id").asText();


        String bodyError = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                           "id": {id},
                           "balance": {balance} 
                         }""".replace("{id}", accountId).replace("{balance}", amount))
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST).extract()
                .response().
                getBody().asString();
        assertEquals(bodyError, error);
    }

    @Test
    public void userDepositMoneyWithinValidAccountTest(){
        String name = generate();
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
                .response()
                .header("Authorization");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                           "id": "10,
                           "balance": 3000
                         }""")
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }


}
