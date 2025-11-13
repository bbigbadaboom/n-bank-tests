package Common.Storage;

import API.Models.CreateUserRequest;
import API.skelethon.Steps.UserSteps;

import java.util.*;

public class SessionStorage {
    private static final ThreadLocal<SessionStorage> INTSTANCE = ThreadLocal.withInitial(SessionStorage::new);
    private final LinkedHashMap<CreateUserRequest, UserSteps> userStepsMap = new LinkedHashMap<>();
    private SessionStorage(){
    }
    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user: users) {
            INTSTANCE.get().userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
    }
    public static CreateUserRequest getUser(int number) {
        return new ArrayList<>(INTSTANCE.get().userStepsMap.keySet()).get(number-1);

    }

    public static List<CreateUserRequest> getUsers() {
        return new ArrayList<>(INTSTANCE.get().userStepsMap.keySet());

    }
    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INTSTANCE.get().userStepsMap.values()).get(number-1);
    }

    public static UserSteps getSteps() {
        return getSteps(1);
    }

    public static void clear() {
        INTSTANCE.get().userStepsMap.clear();
    }
}
