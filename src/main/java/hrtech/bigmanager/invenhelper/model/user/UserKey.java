package hrtech.bigmanager.invenhelper.model.user;

import hrtech.bigmanager.invenhelper.exception.InvalidBusinessIdentifier;
import hrtech.bigmanager.invenhelper.model.DomainKey;

import java.util.Objects;
import java.util.UUID;

public class UserKey implements DomainKey<UserKey> {

    private final UUID databaseKey;
    private String username;

    /**
     * This constructor creates a User key, using a business key (the username) and generates a database key (UUID)
     *
     * @param username username to use
     * @throws InvalidBusinessIdentifier if the username is invalid
     */
    public UserKey(String username) throws InvalidBusinessIdentifier {
        this.databaseKey = DomainKey.generateID();
        if (validBusinessKey(username)) {
            this.username = username;
        } else {
            throw new InvalidBusinessIdentifier(username);
        }
    }

    /**
     * This constructor creates a Product key, using a business key (that is tested) and the database key
     *
     * @param username username to use
     * @throws InvalidBusinessIdentifier if the internal key is invalid
     */
    public UserKey(UUID databaseKey, String username) throws InvalidBusinessIdentifier {
        this.databaseKey = databaseKey;
        if (validBusinessKey(username)) {
            this.username = username;
        } else {
            throw new InvalidBusinessIdentifier(username);
        }
    }

    public UUID getDatabaseKey() {
        return databaseKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws InvalidBusinessIdentifier {
        if (validBusinessKey(username)) {
            this.username = username;
        } else {
            throw new InvalidBusinessIdentifier(username);
        }
    }

    /**
     * Method that validates a text
     *
     * @param text text to be checked
     * @return true on success
     */
    private boolean validBusinessKey(String text) {
        return !text.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserKey userKey = (UserKey) o;
        return getDatabaseKey().equals(userKey.getDatabaseKey()) && getUsername().equals(userKey.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDatabaseKey(), getUsername());
    }
}
