package iteration2;

import Models.CreateUserRequest;
import Models.GetUserProfile;
import Models.Roles;
import Models.UserChangeNameRequest;
import Requests.AdminCreateUserRequest;
import Requests.UserChangeProfileNameRequest;
import Requests.UserGetHisProfileRequest;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static Generates.Common.generateName;
import static Generates.Common.generatePassword;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNull;


public class ChangeAccountNameTest {
    private static Stream<Arguments> inValidData() {
        return Stream.of(
                Arguments.of("one word", "NewNAme", "Name must contain two words with letters only"),
                Arguments.of("using digits", "New1 Name", "Name must contain two words with letters only"),
                Arguments.of("using special charactrs", "New Name!", "Name must contain two words with letters only"),
                Arguments.of("Just spaces", "     ", "Name must contain two words with letters only")

        );
    }

    @Test
    public void changeUsersNameTest() {
        String name = generateName();
        String pass = generatePassword(10);
        String auth;
        UserChangeNameRequest userChangeNameRequest = UserChangeNameRequest
                .builder()
                .name("New Name")
                .build();
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(name)
                .password(pass)
                .role(Roles.USER.toString())
                .build();
        auth = new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest)
                .extract()
                .header("Authorization");

        String nameAfterChange = new UserChangeProfileNameRequest(RequestSpecs.userAuthSpec(auth), ResponseSpecs.getOkStatus())
                .put(userChangeNameRequest)
                .extract()
                .response().jsonPath().get("customer.name").toString();
        assertEquals(nameAfterChange, "New Name");

        String nameInProdileRequest = new UserGetHisProfileRequest(RequestSpecs.userAuthSpec(auth), ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .response().jsonPath().get("name").toString();
        assertEquals(nameInProdileRequest, "New Name");
    }

    @ParameterizedTest(name = "{displayName} {0}")
    @MethodSource("inValidData")
    public void changeUsersNameWithInvalidDataTest(String testName, String name, String error) {
        String username = generateName();
        String pass = generatePassword(10);
        String auth;
        UserChangeNameRequest userChangeNameRequest = UserChangeNameRequest
                .builder()
                .name(name)
                .build();
        CreateUserRequest createUserRequest = CreateUserRequest
                .builder()
                .username(username)
                .password(pass)
                .role(Roles.USER.toString())
                .build();
        auth = new AdminCreateUserRequest(RequestSpecs.adminAuthSpec(), ResponseSpecs.entityCreated())
                .post(createUserRequest)
                .extract()
                .header("Authorization");

        String bodyError = new UserChangeProfileNameRequest(RequestSpecs.userAuthSpec(auth), ResponseSpecs.getBadReqStatus())
                .put(userChangeNameRequest)
                .extract()
                .response().getBody().asString();
        assertEquals(bodyError, error);

        GetUserProfile getUserProfile = new UserGetHisProfileRequest(RequestSpecs.userAuthSpec(auth), ResponseSpecs.getOkStatus())
                .get()
                .extract()
                .response().as(GetUserProfile.class);
        assertNull(getUserProfile.getName());
    }

}
