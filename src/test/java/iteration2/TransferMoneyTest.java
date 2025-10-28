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

import java.util.List;
import java.util.stream.Stream;

import static Utils.Common.generate;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransferMoneyTest {
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );
    }

    private static Stream<Arguments> inValidAmountData() {
        return Stream.of(
                Arguments.of("amount more than 10000", "10000.01", "Transfer amount cannot exceed 10000"),
                Arguments.of("amount 0", "0", "Transfer amount must be at least 0.01"),
                Arguments.of("amount -0.01", "-0.01", "Transfer amount must be at least 0.01"),
                Arguments.of("amount 10001", "10001", "Transfer amount cannot exceed 10000")
        );
    }


    @Test
    public void transferMoneyBetweenUsersAccountsTest() throws JsonProcessingException {
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

        Response responseForSecondAccount = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        JsonNode secondAccountBody = objectMapper.readTree(responseForSecondAccount.getBody().asString());
        String secondAccountId = secondAccountBody.get("id").asText();
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                           "id": {id},
                           "balance": 3000 
                         }""".replace("{id}", accountId))
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        Response responseForTransfer = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                          "senderAccountId": {id},
                          "receiverAccountId": {secondId},
                          "amount": 0.01
                        }""".replace("{id}", accountId).replace("{secondId}", secondAccountId))
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK).extract()
                .response();
        JsonNode nodeForTransfer = objectMapper.readTree(responseForTransfer.getBody().asString());
        assertEquals(nodeForTransfer.get("senderAccountId").asText(), accountId);
        assertEquals(nodeForTransfer.get("message").asText(), "Transfer successful");
        assertEquals(nodeForTransfer.get("amount").asText(), "0.01");
        assertEquals(nodeForTransfer.get("receiverAccountId").asText(), secondAccountId);

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
        assertEquals(Double.parseDouble(nodeForGetAccounts.get(0).get("balance").toString()), 2999.99);
        assertEquals(Double.parseDouble(nodeForGetAccounts.get(1).get("balance").toString()), 0.01);
    }

    @Test
    public void transferMoneyBetweenDifferentUsersAccounts() throws JsonProcessingException {
        String name = generate();
        String secondName = generate();
        ObjectMapper objectMapper = new ObjectMapper();
        String accountId;
        String authForUserOne = given()
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

        String authForUserTwo = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "{userName}",
                          "password": "Kate2000!"   ,
                          "role": "USER"
                        }""".replace("{userName}", secondName))
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
                .header("Authorization", authForUserOne)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();


        JsonNode body = objectMapper.readTree(response.getBody().asString());
        accountId = body.get("id").asText();

        Response responseForUserTwo = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", authForUserTwo)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        JsonNode bodyForUserTwo = objectMapper.readTree(responseForUserTwo.getBody().asString());
        String accountIdForUserTwo = bodyForUserTwo.get("id").asText();

        for(int i = 0; i < 3; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("Authorization", authForUserOne)
                    .body("""
                        {
                           "id": {id},
                           "balance": 5000 
                         }""".replace("{id}", accountId))
                    .when()
                    .post("http://localhost:4111/api/v1/accounts/deposit")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);
        }

        Response responseForTransfer = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", authForUserOne)
                .body("""
                        {
                          "senderAccountId": {id},
                          "receiverAccountId": {secondId},
                          "amount": 10000
                        }""".replace("{id}", accountId).replace("{secondId}", accountIdForUserTwo))
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK).extract()
                .response();
        JsonNode nodeForTransfer = objectMapper.readTree(responseForTransfer.getBody().asString());
        assertEquals(nodeForTransfer.get("senderAccountId").asText(), accountId);
        assertEquals(nodeForTransfer.get("message").asText(), "Transfer successful");
        assertEquals(nodeForTransfer.get("amount").asText(), "10000.0");
        assertEquals(nodeForTransfer.get("receiverAccountId").asText(), accountIdForUserTwo);

        JsonNode nodeForGetAccountForUserOne = objectMapper.readTree(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", authForUserOne)
                .when(  )
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response().
                getBody().asString());
        assertEquals(Double.parseDouble(nodeForGetAccountForUserOne.get(0).get("balance").toString()), 5000.0);

        JsonNode nodeForGetAccountForUserTwo = objectMapper.readTree(given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", authForUserTwo)
                .when(  )
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response().
                getBody().asString());
        assertEquals(Double.parseDouble(nodeForGetAccountForUserTwo.get(0).get("balance").toString()), 10000.0);
    }

    @Test
    public void transferMoneyFromInvalidAccountTest() throws JsonProcessingException {
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
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                           "id": {id},
                           "balance": 3000 
                         }""".replace("{id}", accountId))
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
                          "senderAccountId": -10,
                          "receiverAccountId": {id},
                          "amount": 0.01
                        }""".replace("{id}", accountId))
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void transferMoneyToInvalidAccountTest() throws JsonProcessingException {
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
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                           "id": {id},
                           "balance": 3000 
                         }""".replace("{id}", accountId))
                .when()
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        String bodyError = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                          "senderAccountId": {id},
                          "receiverAccountId": 10000000,
                          "amount": 0.01
                        }""".replace("{id}", accountId))
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST).extract()
                .response().getBody().asString();
        assertEquals(bodyError, "Invalid transfer: insufficient funds or invalid accounts");
    }

    @ParameterizedTest(name = "{displayName} {0}")
    @MethodSource("inValidAmountData")
    public void transferMoneywithInvalidAmountTest(String testName, String amount, String error) throws JsonProcessingException {
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

        Response responseForSecondAccount = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        JsonNode secondAccountBody = objectMapper.readTree(responseForSecondAccount.getBody().asString());
        String secondAccountId = secondAccountBody.get("id").asText();
        for (int i = 0; i < 3; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("Authorization", auth)
                    .body("""
                            {
                               "id": {id},
                               "balance": 5000 
                             }""".replace("{id}", accountId))
                    .when()
                    .post("http://localhost:4111/api/v1/accounts/deposit")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);
        }

        String bodyError = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                          "senderAccountId": {id},
                          "receiverAccountId": {secondId},
                          "amount": {amount}
                        }""".replace("{id}", accountId).replace("{secondId}", secondAccountId)
                        .replace("{amount}", amount))
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST).extract()
                .response().getBody().asString();
        assertEquals(bodyError, error);

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
        assertTrue((nodeForGetAccounts.get(0).get("balance").toString().equals("15000.0") ||
                nodeForGetAccounts.get(1).get("balance").toString().equals("15000.0")) &&
                (nodeForGetAccounts.get(0).get("balance").toString().equals("0.0") ||
                nodeForGetAccounts.get(1).get("balance").toString().equals("0.0")));
    }

    @Test
    public void transferMoneywithAmountMoreThanBalanceTest() throws JsonProcessingException {
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

        Response responseForSecondAccount = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .when()
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        JsonNode secondAccountBody = objectMapper.readTree(responseForSecondAccount.getBody().asString());
        String secondAccountId = secondAccountBody.get("id").asText();
            given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .header("Authorization", auth)
                    .body("""
                            {
                               "id": {id},
                               "balance": 5000 
                             }""".replace("{id}", accountId))
                    .when()
                    .post("http://localhost:4111/api/v1/accounts/deposit")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);

        String bodyError = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", auth)
                .body("""
                        {
                          "senderAccountId": {id},
                          "receiverAccountId": {secondId},
                          "amount": 7000
                        }""".replace("{id}", accountId).replace("{secondId}", secondAccountId)
                )
                .when()
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST).extract()
                .response().getBody().asString();
        assertEquals(bodyError, "Invalid transfer: insufficient funds or invalid accounts");

        JsonNode nodeForGetAccount = objectMapper.readTree(given()
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
        assertEquals(Double.parseDouble(nodeForGetAccount.get(0).get("balance").toString()), 5000.0);
        assertEquals(Double.parseDouble(nodeForGetAccount.get(1).get("balance").toString()), 0.0);
    }
}
