package hrtech.bigmanager.invenhelper.service;

import hrtech.bigmanager.invenhelper.exception.*;
import hrtech.bigmanager.invenhelper.model.DomainKey;
import hrtech.bigmanager.invenhelper.model.Response;
import hrtech.bigmanager.invenhelper.model.user.User;
import hrtech.bigmanager.invenhelper.model.user.UserKey;
import hrtech.bigmanager.invenhelper.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * User service
 */
@Service
public class UserService implements IService<User, UserKey> {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;

    @Autowired
    public void setProductRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Method that creates a new entity
     *
     * @param objectToSave entity to be created
     * @return entity created
     */
    @Override
    public boolean insert(User objectToSave) {
        return userRepository.insert(objectToSave);
    }

    /**
     * Method that updates an entity
     *
     * @param objectToSave entity to be updated
     * @return entity updated
     */
    @Override
    public boolean save(User objectToSave) {
        return userRepository.save(objectToSave);
    }

    /**
     * Method that, given a key object, returns the corresponding Entity on an Optional
     *
     * @param keyToSearch valid object to search the entity
     * @return Option that contains the entity, if such ID exists on the database
     */
    @Override
    public Optional<User> findById(Object keyToSearch) {
        try {
            UserKey key = new UserKey(DomainKey.convertStringToUUID(keyToSearch.toString()), keyToSearch.toString());
            return userRepository.findById(key);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid key: " + keyToSearch);
            return Optional.empty();
        }
    }

    /**
     * Method that, given a key object, returns the corresponding object using the username
     *
     * @param keyToSearch valid key object to search the entity
     * @return Option that contains the entity, if such ID exists on the database
     */
    public Optional<User> findByUsername(String keyToSearch) {
        try {
            UserKey key = new UserKey(UUID.randomUUID(), keyToSearch);
            return userRepository.findByUsername(key);
        } catch (InvalidBusinessIdentifier ibi) {
            return Optional.empty();
        }
    }

    /**
     * Method that, receiving a valid JSON object, creates a new User and saves it on the database
     *
     * @param objectOnBody JSON body with info about the user for the creation
     * @return Response object with info about the success of the operation
     */
    public Response<User> createNewUser(JSONObject objectOnBody) {
        String requiringUsername = objectOnBody.optString("requiring_user");
        try {
            Optional<User> requiringUser = this.findByUsername(requiringUsername);
            if (requiringUser.isEmpty()) {
                throw new UserDoesNotExist("Requester username not found: " + requiringUsername);
            }
            if (!requiringUser.get().checkUserPermission(User.UserPermission.CAN_MODIFY_USERS.getPermissionName())) {
                throw new UserNotAllowed(requiringUsername, "create user");
            }

            User user = User.convertFromJSONToCreate(objectOnBody);
            if (this.findByUsername(user.getUsername()).isPresent()) {
                throw new InvalidBusinessIdentifier("A user with the same username is already registered");
            }

            boolean answer = this.insert(user);
            return new Response<>(answer, (answer ? "Success creating the user" : "Error saving on the database"), (answer ? user : null));
        } catch (InvalidRepresentationOfConceptOnJSON e) {
            logger.error("Invalid JSON object to be converted to User: " + e.getLocalizedMessage());
            return new Response<>(false, "Error converting the JSON into a User. Check the request");
        } catch (UserNotAllowed | InvalidBusinessIdentifier e) {
            logger.error(e.getLocalizedMessage());
            return new Response<>(false, e.getLocalizedMessage());
        }
    }

    /**
     * Method that updates the user information (username, name and permissions).
     *
     * @param info JSON object with info to update. It must contain the 'requiring_user' and 'username'; 'name' and 'permissions' are optional
     * @return Response with info about the success of the operation
     */
    public Response<User> updateUserInformation(JSONObject info) {
        String requiringUsername = info.optString("requiring_user");
        String username = info.optString("username", "");
        try {
            Optional<User> requiringUser = this.findByUsername(requiringUsername);
            if (requiringUser.isEmpty()) {
                throw new UserDoesNotExist("Requester username not found: " + requiringUsername);
            }
            if (!requiringUser.get().checkUserPermission(User.UserPermission.CAN_MODIFY_USERS.getPermissionName()) && !username.equals(requiringUsername)) {
                throw new UserNotAllowed(requiringUsername, "update user");
            }

            Optional<User> userToUpdate = findByUsername(username);
            if (userToUpdate.isEmpty()) {
                return new Response<>(false, "User not found");
            }

            boolean hasChanges = false;

            User oldUser = userToUpdate.get();
            if (info.has("name")) {
                oldUser.updateName(info.optString("name", ""));
                hasChanges = true;
            }
            if (info.has("permissions")) {
                JSONArray permissionMappingOnObject = info.getJSONArray("permissions");
                for (int index = 0; index < permissionMappingOnObject.length(); index++) {
                    JSONObject currentPermissionInfo = permissionMappingOnObject.getJSONObject(index);
                    String permissionName = currentPermissionInfo.optString("name", "");
                    boolean permissionValue = currentPermissionInfo.optBoolean("value", false);

                    oldUser.addEntryToPermissionMap(permissionName, permissionValue);
                }
                hasChanges = true;
            }

            if (!hasChanges) {
                return new Response<>(true, "No information to update user");
            }
            if (this.save(oldUser)) {
                return new Response<>(true, "User updated", oldUser);
            } else {
                return new Response<>(false, "Error updating database", userToUpdate.get());
            }
        } catch (InvalidText it) {
            return new Response<>(false, it.getLocalizedMessage());
        } catch (UserNotAllowed | UserDoesNotExist ex) {
            logger.error(ex.getMessage());
            return new Response<>(false, ex.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error(e.getLocalizedMessage());
            return new Response<>(false, "Error while updating product information");
        }
    }


    /**
     * Method that deletes a user from the database, given a JSON object with the requiring user and the user to delete
     * <p>
     * It is checked the permission of the requiring user (or if corresponds to the user to delete)
     *
     * @param info JSONObject with two keys: requiring_user and user_to_delete
     * @return Response with info about the operation
     */
    public Response<User> deleteUser(JSONObject info) {
        String requiringUsername = info.optString("requiring_user");
        String usernameToDelete = info.optString("user_to_delete");

        try {
            Optional<User> requiringUser = this.findByUsername(requiringUsername);
            if (requiringUser.isEmpty()) {
                throw new UserDoesNotExist("Requester username not found: " + requiringUsername);
            }
            if (!requiringUser.get().checkUserPermission(User.UserPermission.CAN_MODIFY_USERS.getPermissionName()) && !usernameToDelete.equals(requiringUsername)) {
                throw new UserNotAllowed(requiringUsername, "delete user");
            }

            Optional<User> userToDelete = this.findByUsername(usernameToDelete);
            if (userToDelete.isEmpty()) {
                throw new UserDoesNotExist("Username to delete not found: " + requiringUsername);
            }

            if (userRepository.delete(userToDelete.get())) {
                return new Response<>(true);
            } else {
                return new Response<>(false, "Error while deleting user");
            }
        } catch (UserNotAllowed | UserDoesNotExist ex) {
            logger.error(ex.getMessage());
            return new Response<>(false, ex.getMessage());
        }
    }
}
