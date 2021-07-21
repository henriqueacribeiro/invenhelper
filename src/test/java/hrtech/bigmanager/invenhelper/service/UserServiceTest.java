package hrtech.bigmanager.invenhelper.service;

import hrtech.bigmanager.invenhelper.config.InvenHelperApplication;
import hrtech.bigmanager.invenhelper.model.Response;
import hrtech.bigmanager.invenhelper.model.user.User;
import hrtech.bigmanager.invenhelper.model.user.UserInformation;
import hrtech.bigmanager.invenhelper.model.user.UserKey;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = "classpath:databaseinit.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(classes = InvenHelperApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {

    @Autowired
    private UserService service;

    private UUID defaultDatabaseCode;
    private String defaultCode;
    private UserKey defaultKey;

    private String defaultName;
    private UserInformation defaultInformation;

    private User user;

    private String generateString() {
        //Baeldung Random String generation
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private boolean generateRandomBoolean() {
        Random random = new Random();
        int randomInt = random.ints(1, 1000).findFirst().getAsInt();
        return randomInt % 2 == 0;
    }

    @BeforeEach
    void setUp() {
        defaultName = generateString();
        defaultInformation = new UserInformation(defaultName);

        defaultCode = generateString();
        defaultDatabaseCode = UUID.randomUUID();
        defaultKey = new UserKey(defaultDatabaseCode, defaultCode);

        user = new User(defaultKey, defaultInformation);
        user.addEntryToPermissionMap(User.UserPermission.CAN_MODIFY_PRODUCTS.getPermissionName(), generateRandomBoolean());
        user.addEntryToPermissionMap(User.UserPermission.CAN_MODIFY_USERS.getPermissionName(), generateRandomBoolean());
        user.addEntryToPermissionMap(User.UserPermission.CAN_MODIFY_INVENTORY.getPermissionName(), generateRandomBoolean());
    }

    @Test
    void findById() {
        assertTrue(service.insert(user));
        Optional<User> obtainedUser = service.findById(defaultKey.getDatabaseKey());
        assertTrue(obtainedUser.isPresent());
        assertEquals(user, obtainedUser.get());
        assertTrue(user.sameAs(obtainedUser.get()));
    }

    @Test
    void findByUsername() {
        assertTrue(service.insert(user));
        Optional<User> obtainedUser = service.findByUsername(defaultKey.getUsername());
        assertTrue(obtainedUser.isPresent());
        assertEquals(user, obtainedUser.get());
        assertTrue(user.sameAs(obtainedUser.get()));
    }

    @Test
    void createNewUserValid() {
        String name = generateString();
        UserInformation information = new UserInformation(name);

        String username = generateString();
        UUID databaseCode = UUID.randomUUID();
        UserKey key = new UserKey(databaseCode, username);

        User user = new User(key, information);
        user.addEntryToPermissionMap(User.UserPermission.CAN_MODIFY_PRODUCTS.getPermissionName(), generateRandomBoolean());
        user.addEntryToPermissionMap(User.UserPermission.CAN_MODIFY_USERS.getPermissionName(), generateRandomBoolean());
        user.addEntryToPermissionMap(User.UserPermission.CAN_MODIFY_INVENTORY.getPermissionName(), generateRandomBoolean());

        Map<User.UserPermission, Boolean> permissions = new HashMap<>();
        for (int i = 0; i < User.UserPermission.values().length; i++) {
            permissions.put(User.UserPermission.values()[i], user.checkUserPermission(User.UserPermission.values()[i].getPermissionName()));
        }

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", username);
        objectToInject.put("name", name);
        JSONArray arrays = new JSONArray();
        for (User.UserPermission currentPermission : permissions.keySet()) {
            JSONObject otherPermission = new JSONObject();
            otherPermission.put("name", currentPermission.getPermissionName());
            otherPermission.put("value", user.checkUserPermission(currentPermission.getPermissionName()));
            arrays.put(otherPermission);
        }
        objectToInject.put("permissions", arrays);

        Response<User> response = service.createNewUser(objectToInject);
        assertTrue(response.isSuccess());
        assertEquals(user, response.getObjectToReturn());

        Optional<User> userOnDatabase = service.findByUsername(username);
        assertTrue(userOnDatabase.isPresent());
        assertEquals(user, userOnDatabase.get());
    }

    @Test
    void createNewUserValidWithoutPermissions() {
        String name = generateString();
        UserInformation information = new UserInformation(name);

        String username = generateString();
        UUID databaseCode = UUID.randomUUID();
        UserKey key = new UserKey(databaseCode, username);

        User user = new User(key, information);

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", username);
        objectToInject.put("name", name);

        Response<User> response = service.createNewUser(objectToInject);
        assertTrue(response.isSuccess());
        assertEquals(user, response.getObjectToReturn());

        Optional<User> userOnDatabase = service.findByUsername(username);
        assertTrue(userOnDatabase.isPresent());
        assertEquals(user, userOnDatabase.get());

        for (int i = 0; i < User.UserPermission.values().length; i++) {
            assertFalse(userOnDatabase.get().checkUserPermission(User.UserPermission.values()[i].getPermissionName()));
            assertFalse(response.getObjectToReturn().checkUserPermission(User.UserPermission.values()[i].getPermissionName()));
        }
    }

    @Test
    void createNewUserInvalidName() {
        String name = generateString();

        String username = "";

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", generateString());
        objectToInject.put("name", "");

        Response<User> response = service.createNewUser(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("Error converting the JSON into a User. Check the request", response.getAdditionalInformation());
        assertFalse(service.findByUsername(username).isPresent());
    }

    @Test
    void createNewUserInvalidUsername() {
        String name = generateString();
        String username = "";

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", "");
        objectToInject.put("name", name);

        Response<User> response = service.createNewUser(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("Error converting the JSON into a User. Check the request", response.getAdditionalInformation());
        assertFalse(service.findByUsername(username).isPresent());
    }

    @Test
    void createNewUserUsedUsername() {
        assertTrue(service.insert(user));
        String username = user.getUsername();

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", username);
        objectToInject.put("name", generateString());

        Response<User> response = service.createNewUser(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("A user with the same username is already registered", response.getAdditionalInformation());
        Optional<User> userOpt = service.findByUsername(username);
        assertTrue(userOpt.isPresent());
        assertEquals(user, userOpt.get());
    }

    @Test
    void createNewUserNoPermissionToUpdate() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", "admin");
        objectToInject.put("name", "adminChanged");
        JSONArray arrays = new JSONArray();
        JSONObject permissionToChange = new JSONObject();
        permissionToChange.put("name", User.UserPermission.CAN_MODIFY_USERS);
        permissionToChange.put("value", false);
        arrays.put(permissionToChange);
        objectToInject.put("permissions", arrays);
        assertTrue(service.updateUserInformation(objectToInject).isSuccess());

        objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("name", generateString());
        objectToInject.put("username", user.getUsername());
        objectToInject.put("permissions", arrays);

        Response<User> response = service.createNewUser(objectToInject);
        assertFalse(response.isSuccess());
        Optional<User> foundUser = service.findByUsername(user.getUsername());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void updateUserInformationChangeName() {
        assertTrue(service.insert(user));
        String name = generateString();
        String username = user.getUsername();

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", username);
        objectToInject.put("name", name);

        user.updateName(name);

        Response<User> response = service.updateUserInformation(objectToInject);
        assertTrue(response.isSuccess());
        assertEquals(user, response.getObjectToReturn());

        Optional<User> userOnDatabase = service.findByUsername(username);
        assertTrue(userOnDatabase.isPresent());
        assertEquals(user, userOnDatabase.get());
    }

    @Test
    void updateUserInformationChangePermissions() {
        assertTrue(service.insert(user));
        String name = user.getName();
        String username = user.getUsername();

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", username);
        objectToInject.put("name", name);
        JSONArray arrays = new JSONArray();
        for (User.UserPermission currentPermission : User.UserPermission.getListOfExistingPermissions()) {
            JSONObject otherPermission = new JSONObject();
            otherPermission.put("name", currentPermission.getPermissionName());
            otherPermission.put("value", generateRandomBoolean());
            arrays.put(otherPermission);
            user.addEntryToPermissionMap(currentPermission.getPermissionName(), otherPermission.optBoolean("value", false));
        }
        objectToInject.put("permissions", arrays);

        Response<User> response = service.updateUserInformation(objectToInject);
        assertTrue(response.isSuccess());
        assertEquals(user, response.getObjectToReturn());

        Optional<User> userOnDatabase = service.findByUsername(username);
        assertTrue(userOnDatabase.isPresent());
        assertEquals(user, userOnDatabase.get());
    }

    @Test
    void updateUserInformationInvalidUsername() {
        assertTrue(service.insert(user));

        String name = generateString();
        String username = user.getUsername();

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", "");
        objectToInject.put("name", name);

        Response<User> response = service.updateUserInformation(objectToInject);
        assertFalse(response.isSuccess());
        assertEquals("User not found", response.getAdditionalInformation());

        Optional<User> userOnDatabase = service.findByUsername(username);
        assertTrue(userOnDatabase.isPresent());
        assertEquals(user, userOnDatabase.get());
    }

    @Test
    void updateUserInformationNoPermissionToUpdate() {
        assertTrue(service.insert(user));

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", "admin");
        objectToInject.put("name", "adminChanged");
        JSONArray arrays = new JSONArray();
        JSONObject permissionToChange = new JSONObject();
        permissionToChange.put("name", User.UserPermission.CAN_MODIFY_USERS);
        permissionToChange.put("value", false);
        arrays.put(permissionToChange);
        objectToInject.put("permissions", arrays);
        assertTrue(service.updateUserInformation(objectToInject).isSuccess());

        objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("name", generateString());
        objectToInject.put("username", user.getUsername());

        Response<User> response = service.updateUserInformation(objectToInject);
        assertFalse(response.isSuccess());
        Optional<User> foundUser = service.findByUsername(user.getUsername());
        assertTrue(foundUser.isPresent());
        assertNotEquals(objectToInject.get("name"), foundUser.get().getName());
        assertEquals(user.getName(), foundUser.get().getName());
    }

    @Test
    void deleteUserSelfDelete() {
        assertTrue(service.insert(user));
        Optional<User> registeredUser = service.findByUsername(user.getUsername());
        assertTrue(registeredUser.isPresent());

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", user.getUsername());
        objectToInject.put("user_to_delete", user.getUsername());

        Response<User> response = service.deleteUser(objectToInject);
        assertTrue(response.isSuccess());
        Optional<User> notFoundUser = service.findByUsername(user.getUsername());
        assertTrue(notFoundUser.isEmpty());
    }

    @Test
    void deleteUserAdminDelete() {
        assertTrue(service.insert(user));
        Optional<User> registeredUser = service.findByUsername(user.getUsername());
        assertTrue(registeredUser.isPresent());

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("user_to_delete", user.getUsername());

        Response<User> response = service.deleteUser(objectToInject);
        assertTrue(response.isSuccess());
        Optional<User> notFoundUser = service.findByUsername(user.getUsername());
        assertTrue(notFoundUser.isEmpty());
    }

    @Test
    void deleteUserUserToDeleteNotFound() {
        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("user_to_delete", user.getUsername());

        Response<User> response = service.deleteUser(objectToInject);
        assertFalse(response.isSuccess());
        Optional<User> foundUser = service.findByUsername(user.getUsername());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void deleteUserNoPermissionToDelete() {
        assertTrue(service.insert(user));

        JSONObject objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("username", "admin");
        objectToInject.put("name", "adminChanged");
        JSONArray arrays = new JSONArray();
        JSONObject permissionToChange = new JSONObject();
        permissionToChange.put("name", User.UserPermission.CAN_MODIFY_USERS);
        permissionToChange.put("value", false);
        arrays.put(permissionToChange);
        objectToInject.put("permissions", arrays);
        assertTrue(service.updateUserInformation(objectToInject).isSuccess());

        objectToInject = new JSONObject();
        objectToInject.put("requiring_user", "admin");
        objectToInject.put("user_to_delete", user.getUsername());

        Response<User> response = service.deleteUser(objectToInject);
        assertFalse(response.isSuccess());
        Optional<User> foundUser = service.findByUsername(user.getUsername());
        assertTrue(foundUser.isPresent());
    }
}