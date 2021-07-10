package hrtech.bigmanager.invenhelper.model.user;

import hrtech.bigmanager.invenhelper.exception.InvalidBusinessIdentifier;
import hrtech.bigmanager.invenhelper.exception.InvalidText;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private UUID defaultDatabaseCode;
    private String defaultUsername;
    private UserKey defaultKey;

    private String defaultName;
    private UserInformation defaultInformation;

    private Map<String, Boolean> permissionMap;

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

    @BeforeEach
    void setUp() {
        Random random = new Random();
        int permissionNumber = random.ints(0, 1000).findFirst().getAsInt();
        boolean currentPermission = false;

        permissionMap = new HashMap<>();
        for (int i = 0; i < permissionNumber; i++) {
            permissionMap.put(generateString(), currentPermission);
            currentPermission = !currentPermission;
        }

        defaultName = generateString();
        defaultInformation = new UserInformation(defaultName);

        defaultUsername = generateString();
        defaultDatabaseCode = UUID.randomUUID();
        defaultKey = new UserKey(defaultDatabaseCode, defaultUsername);


        user = new User(defaultKey, defaultInformation, permissionMap);
    }

    @Test
    void updateUsernameValid() {
        assertEquals(defaultUsername, user.getUsername());
        String newUsername = generateString();
        user.updateUsername(newUsername);
        assertEquals(newUsername, user.getUsername());
    }

    @Test
    void updateUsernameInvalid() {
        assertThrows(InvalidBusinessIdentifier.class, () -> user.updateUsername(""));
    }

    @Test
    void updateNameValid() {
        assertEquals(defaultName, user.getName());
        String newName = generateString();
        user.updateName(newName);
        assertEquals(newName, user.getName());
    }

    @Test
    void updateNameInvalid() {
        assertThrows(InvalidText.class, () -> user.updateName(""));
    }

    @Test
    void addEntryToPermissionMapValidEntry() {
        String permissionName = generateString();
        user.addEntryToPermissionMap(permissionName, true);
        assertTrue(user.checkUserPermission(permissionName));
    }

    @Test
    void addEntryToPermissionMapInvalidEntry() {
        assertThrows(InvalidText.class, () -> user.addEntryToPermissionMap("", true));
    }

    @Test
    void checkUserPermissionExists() {
        String permissionName;
        if (permissionMap.isEmpty()) {
            permissionName = generateString();
            user.addEntryToPermissionMap(permissionName, true);
            assertTrue(user.checkUserPermission(permissionName));
        } else {
            permissionName = permissionMap.keySet().stream().findFirst().get();
        }
        assertEquals(permissionMap.get(permissionName), user.checkUserPermission(permissionName));
    }

    @Test
    void checkUserPermissionInvalid() {
        assertFalse(user.checkUserPermission(""));
    }

    @Test
    void convertFromJSONToCreateValid() {
        JSONObject creationObject = new JSONObject();
        creationObject.put("username", defaultUsername);
        creationObject.put("name", defaultName);

        JSONArray permissionArray = new JSONArray();
        for (Map.Entry<String, Boolean> entry : permissionMap.entrySet()) {
            JSONObject permission = new JSONObject();
            permission.put("name", entry.getKey());
            permission.put("value", entry.getValue());
            permissionArray.put(permission);
        }
        creationObject.put("permissions", permissionArray);

        User user = User.convertFromJSONToCreate(creationObject);
        assertEquals(user, this.user);
    }

    @Test
    void convertToJSON() {
        JSONObject creationObject = new JSONObject();
        creationObject.put("username", defaultUsername);
        creationObject.put("name", defaultName);

        assertEquals(creationObject.toString(), user.convertToJSON().toString());
    }
}