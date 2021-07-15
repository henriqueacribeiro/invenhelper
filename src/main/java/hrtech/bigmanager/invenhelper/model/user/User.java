package hrtech.bigmanager.invenhelper.model.user;

import hrtech.bigmanager.invenhelper.exception.InvalidBusinessIdentifier;
import hrtech.bigmanager.invenhelper.exception.InvalidRepresentationOfConceptOnJSON;
import hrtech.bigmanager.invenhelper.exception.InvalidText;
import hrtech.bigmanager.invenhelper.model.Domain;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * User concept
 */
public class User implements Domain<User, UserKey> {

    //Permission names
    public static final String CAN_MODIFY_INVENTORY = "CAN_MODIFY_INVENTORY";
    public static final String CAN_ADD_USERS = "CAN_ADD_USERS";
    public static final String CAN_MODIFY_PRODUCTS = "CAN_MODIFY_PRODUCTS";

    private final UserKey key;
    private final UserInformation information;
    private final Map<String, Boolean> permissionMap;

    public User(UserKey key, UserInformation information) {
        this.key = key;
        this.information = information;
        this.permissionMap = new HashMap<>();
    }

    public User(UserKey key, UserInformation information, Map<String, Boolean> permissionMap) {
        if (permissionMap == null) {
            throw new IllegalArgumentException("Invalid permission map");
        }
        this.key = key;
        this.information = information;
        this.permissionMap = permissionMap;
    }

    /**
     * Method that converts a JSONObject into the domain concept. It differs from the other method since the object obtained will have the quantity at zero and a new UUID
     *
     * @param jsonObject object to convert
     * @return domain concept built from the JSON representation
     */
    public static User convertFromJSONToCreate(JSONObject jsonObject) throws InvalidRepresentationOfConceptOnJSON {
        String username = jsonObject.optString("username", "");
        String userName = jsonObject.optString("name", "");
        JSONArray permissionMappingOnObject = jsonObject.getJSONArray("permissions");

        try {
            UserKey key = new UserKey(UUID.randomUUID(), username);
            UserInformation info = new UserInformation(userName);
            User user = new User(key, info);

            for (int index = 0; index < permissionMappingOnObject.length(); index++) {
                JSONObject currentPermissionInfo = permissionMappingOnObject.getJSONObject(index);
                String permissionName = currentPermissionInfo.optString("name", "");
                boolean permissionValue = currentPermissionInfo.optBoolean("value", false);

                user.addEntryToPermissionMap(permissionName, permissionValue);
            }

            return user;
        } catch (InvalidText | InvalidBusinessIdentifier it) {
            throw new InvalidRepresentationOfConceptOnJSON(it.getLocalizedMessage());
        }
    }

    public UUID getDatabaseKey() {
        return this.key.getDatabaseKey();
    }

    public String getUsername() {
        return this.key.getUsername();
    }

    public String getName() {
        return information.getName();
    }

    public void updateUsername(String username) throws InvalidBusinessIdentifier {
        key.setUsername(username);
    }

    public void updateName(String name) throws InvalidText {
        information.setName(name);
    }

    /**
     * Method that adds a new entry to the permission map
     *
     * @param permissionName  name of the permission
     * @param permissionValue value of the permission
     * @throws InvalidText if the permission name is empty
     */
    public void addEntryToPermissionMap(String permissionName, boolean permissionValue) throws InvalidText {
        if (permissionName.isBlank()) {
            throw new InvalidText("Invalid permission name");
        }
        permissionMap.put(permissionName, permissionValue);
    }

    /**
     * Method that checks a user permission. If the permission to check does not exist, returns false
     *
     * @param permissionToCheck name of the permission to check
     * @return true or false, depending if a user has the permission; false if the permission does not exist
     */
    public boolean checkUserPermission(String permissionToCheck) {
        return permissionMap.getOrDefault(permissionToCheck, false);
    }

    /**
     * Method that converts a JSONObject into the domain concept
     *
     * @param jsonObject object to convert
     * @return domain concept built from the JSON representation
     */
    @Override
    public User convertFromJSON(JSONObject jsonObject) throws InvalidRepresentationOfConceptOnJSON {
        String username = jsonObject.optString("username", "");
        String userName = jsonObject.optString("name", "");
        JSONArray permissionMappingOnObject = jsonObject.getJSONArray("permissions");

        try {
            UserKey key = new UserKey(UUID.randomUUID(), username);
            UserInformation info = new UserInformation(userName);
            User user = new User(key, info);

            for (int index = 0; index < permissionMappingOnObject.length(); index++) {
                JSONObject currentPermissionInfo = permissionMappingOnObject.getJSONObject(index);
                String permissionName = currentPermissionInfo.optString("name", "");
                boolean permissionValue = currentPermissionInfo.optBoolean("value", false);

                user.addEntryToPermissionMap(permissionName, permissionValue);
            }

            return user;
        } catch (InvalidText | InvalidBusinessIdentifier it) {
            throw new InvalidRepresentationOfConceptOnJSON(it.getLocalizedMessage());
        }
    }

    /**
     * This method converts the domain concept into a JSONObject
     *
     * @return JSONObject representing the domain concept
     */
    @Override
    public JSONObject convertToJSON() {
        JSONObject answer = new JSONObject();
        answer.put("username", key.getUsername());
        answer.put("name", information.getName());
        return answer;
    }

    /**
     * Method that compares if an entity corresponds to the other, by the key
     *
     * @param otherEntity entity to compare
     * @return true if the entities are equal, false otherwise
     */
    @Override
    public boolean sameAs(User otherEntity) {
        return otherEntity.getUsername().equals(getUsername());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return sameAs(user) && information.equals(user.information) && permissionMap.equals(user.permissionMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, information, permissionMap);
    }
}