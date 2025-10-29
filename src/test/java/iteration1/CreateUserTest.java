package iteration1;

import Models.CreateUserRequest;
import Models.CreateUserResponse;
import Models.Roles;
import Requests.AdminCreateUserRequest;
import Requests.AdminGetAllUsersRequest;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import static Generates.Common.generateName;
import static Generates.Common.generatePassword;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CreateUserTest {
    private static Stream<Arguments> invalidData() {
        return Stream.of(
                Arguments.of("blank username", "     ", "Kate1998@", "USER",
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

    @Test
    public void adminCanCreateUserWithCorrectDataTest() {
        String name = generateName();
        String pass = generatePassword(10);
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();

        CreateUserResponse createUserResponse = new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest).extract().as(CreateUserResponse.class);
        assertAll(
                () -> assertEquals(createUserResponse.getUsername(), name),
                () -> assertNotEquals(createUserResponse.getPassword(), pass),
                () -> assertEquals(createUserResponse.getRole(), Roles.USER.toString())
        );
        List<CreateUserResponse> users = new AdminGetAllUsersRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.getOkStatus())
                .get().extract().response().jsonPath().getList("", CreateUserResponse.class);
        List<String> userNames = users.stream().map(CreateUserResponse::getUsername).toList();
        assertTrue(userNames.contains(name));
    }

    @ParameterizedTest(name = "{displayName} {0}")
    @MethodSource("invalidData")
    public void adminCantCreateUserWithInvalidDataTest(String name, String username, String password, String role, String responseBody) {

        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
        new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.getBadReqStatusWithMessage(responseBody))
                .post(createUserRequest);
    }

    @Test
    public void adminCanCreateUserWithCorrectDataButWithExistedUsernameTest() {
        String name = generateName();
        String pass = generatePassword(10);
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();

        new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest);

        String body = new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.getBadReqStatus())
                .post(createUserRequest)
                .extract().body().asString();
        assertEquals(body, "Error: Username '" + name + "' already exists.");
    }
}
