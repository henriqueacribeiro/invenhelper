package hrtech.bigmanager.invenhelper.model.user;

import hrtech.bigmanager.invenhelper.exception.InvalidBusinessIdentifier;
import hrtech.bigmanager.invenhelper.exception.InvalidRepresentationOfConceptOnJSON;
import hrtech.bigmanager.invenhelper.exception.InvalidText;
import hrtech.bigmanager.invenhelper.exception.InvalidUserPermission;
import hrtech.bigmanager.invenhelper.model.Domain;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * User concept
 */
public class User implements Domain<User, UserKey> {

    private final Map<UserPermission, Boolean> permissionMap;

    private final UserKey key;
    private final UserInformation information;
    public User(UserKey key, UserInformation information) {
        this.key = key;
        this.information = information;
        this.permissionMap = new HashMap<>();

        for (int i = 0; i < UserPermission.values().length; i++) {
            permissionMap.put(UserPermission.values()[i], false);
        }
    }

    public User(UserKey key, UserInformation information, Map<UserPermission, Boolean> permissionMap) {
        if (permissionMap == null) {
            throw new IllegalArgumentException("Invalid permission map");
        }
        this.key = key;
        this.information = information;
        this.permissionMap = permissionMap;

        for (int i = 0; i < UserPermission.values().length; i++) {
            if (!permissionMap.containsKey(UserPermission.values()[i])) {
                permissionMap.put(UserPermission.values()[i], false);
            }
        }
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
        JSONArray permissionMappingOnObject = jsonObject.has("permissions") ? jsonObject.optJSONArray("permissions") : new JSONArray();

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
     * Method that adds a new entry to the permission map
     *
     * @param permissionName  name of the permission
     * @param permissionValue value of the permission
     * @throws InvalidUserPermission if the permission name is invalid
     */
    public void addEntryToPermissionMap(String permissionName, boolean permissionValue) throws InvalidText {
        Optional<UserPermission> permissionOptional = UserPermission.convertNameToPermission(permissionName);
        if (permissionOptional.isEmpty()) {
            throw new InvalidUserPermission(permissionName);
        }
        permissionMap.put(permissionOptional.get(), permissionValue);
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
     * Method that checks a user permission. If the permission to check does not exist, returns false
     *
     * @param permissionToCheck name of the permission to check
     * @return true or false, depending if a user has the permission; false if the permission does not exist
     */
    public boolean checkUserPermission(String permissionToCheck) {
        Optional<UserPermission> permissionOptional = UserPermission.convertNameToPermission(permissionToCheck);
        if (permissionOptional.isPresent()) {
            return permissionMap.getOrDefault(permissionOptional.get(), false);
        } else {
            return false;
        }
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
        String name = jsonObject.optString("name", "");
        JSONArray permissionMappingOnObject = jsonObject.getJSONArray("permissions");

        try {
            UserKey key = new UserKey(UUID.randomUUID(), username);
            UserInformation info = new UserInformation(name);
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
     * User permission map
     */
    public enum UserPermission {
        CAN_MODIFY_INVENTORY {
            /**
             * Get permission name
             *
             * @return name of the permission
             */
            @Override
            public String getPermissionName() {
                return "CAN_MODIFY_INVENTORY";
            }

            /**
             * Get permission database field name
             *
             * @return permission database field name
             */
            @Override
            public String getPermissionDatabaseName() {
                return "can_modify_inventory";
            }
        },
        CAN_MODIFY_USERS {
            /**
             * Get permission name
             *
             * @return name of the permission
             */
            @Override
            public String getPermissionName() {
                return "CAN_MODIFY_USERS";
            }

            /**
             * Get permission database field name
             *
             * @return permission database field name
             */
            @Override
            public String getPermissionDatabaseName() {
                return "can_modify_users";
            }
        },
        CAN_MODIFY_PRODUCTS {
            /**
             * Get permission name
             *
             * @return name of the permission
             */
            @Override
            public String getPermissionName() {
                return "CAN_MODIFY_PRODUCTS";
            }

            /**
             * Get permission database field name
             *
             * @return permission database field name
             */
            @Override
            public String getPermissionDatabaseName() {
                return "can_modify_products";
            }
        };

        /**
         * Method that returns a list of existing permissions
         *
         * @return list of existing permissions
         */
        public static List<UserPermission> getListOfExistingPermissions() {
            return Arrays.asList(UserPermission.values());
        }

        /**
         * Method that converts a String representing a name into a permission
         *
         * @param permissionName name of the permission to convert
         * @return Optional with the permission, if found; empty otherwise
         */
        public static Optional<UserPermission> convertNameToPermission(String permissionName) {
            return getListOfExistingPermissions().stream().filter(pm -> pm.getPermissionName().equals(permissionName)).findFirst();
        }

        /**
         * Method that converts a String representing a database field into a permission
         *
         * @param permissionDatabaseName database field of the permission to convert
         * @return Optional with the permission, if found; empty otherwise
         */
        public static Optional<UserPermission> convertDatabaseNameToPermission(String permissionDatabaseName) {
            return getListOfExistingPermissions().stream().filter(pm -> pm.getPermissionDatabaseName().equals(permissionDatabaseName)).findFirst();
        }

        /**
         * Get permission name
         *
         * @return name of the permission
         */
        public String getPermissionName() {
            return "NO_PERMISSION";
        }

        /**
         * Get permission database field name
         *
         * @return permission database field name
         */
        public String getPermissionDatabaseName() {
            return "NO_PERMISSION";
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