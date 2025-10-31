package iteration2;

import Models.*;
import Specs.RequestSpecs;
import Specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import skelethon.EndPoints;
import skelethon.requesters.AdminSteps;
import skelethon.requesters.CrudRequester;
import skelethon.requesters.UserSteps;
import skelethon.requesters.ValidatedCrudRequester;

import static Common.Common.*;
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
