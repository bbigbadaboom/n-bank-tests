package API.iteration1;

import API.Models.CreateUserRequest;
import API.Models.CreateUserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import static API.Common.Common.*;
import static API.Models.Comparisons.ModelAssertions.assertThatModels;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.provider.MethodSource;
import API.skelethon.requesters.AdminSteps;

import java.util.List;
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
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);

        CreateUserResponse createUserResponse = AdminSteps.adminCreateUser(createUserRequest);

        assertAll(
                () -> assertThatModels(CreateUserRequest.class, CreateUserResponse.class),
                () -> assertNotEquals(createUserResponse.getPassword(), createUserRequest.getPassword())
        );
        List<CreateUserResponse> users = AdminSteps.adminGetAllUsers();
        List<String> userNames = users.stream().map(CreateUserResponse::getUsername).toList();
        assertTrue(userNames.contains(createUserRequest.getUsername()));
    }
    @ParameterizedTest(name = "{displayName} {0}")
    @MethodSource("invalidData")
    public void adminCantCreateUserWithInvalidDataTest(String name, String username, String password, String role, String error) {

        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        createUserRequest.setRole(role);
        createUserRequest.setPassword(password);
        createUserRequest.setUsername(username);
        AdminSteps.adminCreateUserWithBadData(createUserRequest, error);
    }

    @Test
    public void adminCanCreateUserWithCorrectDataButWithExistedUsernameTest() {
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);
        AdminSteps.adminCreateUser(createUserRequest);

        assertEquals(AdminSteps.adminCreateUserWithMistake(createUserRequest),
                "Error: Username '" + createUserRequest.getUsername() + "' already exists.");
    }
}
