package Common.Storage;

import API.Models.CreateUserRequest;
import API.Models.CreateUserResponse;
import API.Models.LoginUserResponse;
import API.skelethon.requesters.UserSteps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionStorage {
    private static final SessionStorage INTSTANCE = new SessionStorage();
    private final Map<CreateUserRequest, UserSteps> userStepsMap = new HashMap<>();
    private SessionStorage(){
    }
    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user: users) {
            INTSTANCE.userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
    }
    public static CreateUserRequest getUser(int number) {
        return new ArrayList<>(INTSTANCE.userStepsMap.keySet()).get(number-1);

    }

    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INTSTANCE.userStepsMap.values()).get(number-1);
    }

    public static UserSteps getSteps() {
        return getSteps(1);
    }

    public static void clear() {
        INTSTANCE.userStepsMap.clear();
    }
}
