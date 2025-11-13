package API.iteration2;

import API.BaseTest;
import API.Models.CreateUserRequest;
import API.Models.UserChangeNameRequest;
import API.Models.UserChangeNameResponse;
import API.Models.UserGetHisProfileResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import API.skelethon.Steps.AdminSteps;
import API.skelethon.Steps.UserSteps;

import static Common.Common.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNull;


public class ChangeAccountNameTest extends BaseTest {
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
        UserChangeNameRequest userChangeNameRequest = generate(UserChangeNameRequest.class);
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);

        AdminSteps.adminCreateUser(createUserRequest);

        UserChangeNameResponse userChangeNameResponse =
                UserSteps.userChangeHisName(userChangeNameRequest,createUserRequest.getUsername(), createUserRequest.getPassword());
        assertEquals(userChangeNameResponse.getCustomer().getName(), userChangeNameRequest.getName());

        UserGetHisProfileResponse customer = UserSteps.userGetHisProfile(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertEquals(customer.getName(), userChangeNameRequest.getName());
    }

    @ParameterizedTest(name = "{displayName} {0}")
    @MethodSource("inValidData")
    public void changeUsersNameWithInvalidDataTest(String testName, String name, String error) {
        UserChangeNameRequest userChangeNameRequest = generate(UserChangeNameRequest.class);
        userChangeNameRequest.setName(name);
        CreateUserRequest createUserRequest = generate(CreateUserRequest.class);

        AdminSteps.adminCreateUser(createUserRequest);
        UserSteps.userChangeHisNameWithBadData(userChangeNameRequest, createUserRequest.getUsername(), createUserRequest.getPassword(), error);

        UserGetHisProfileResponse getUserProfile = UserSteps.userGetHisProfile(createUserRequest.getUsername(), createUserRequest.getPassword());
        assertNull(getUserProfile.getName());
    }

}
