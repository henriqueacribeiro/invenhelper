package hrtech.bigmanager.invenhelper.model.user;

import hrtech.bigmanager.invenhelper.exception.InvalidText;

import java.util.Objects;

/**
 * This domain object stores info about the user
 */
public class UserInformation {

    private String name;

    /**
     * Constructor for user information. It checks if the info is valid
     *
     * @param name user name
     * @throws InvalidText if any parameter is invalid
     */
    public UserInformation(String name) throws InvalidText {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidText {
        if (validText(name)) {
            this.name = name;
        } else {
            throw new InvalidText("Invalid name for user: " + name);
        }
    }

    /**
     * Method that validates a text
     *
     * @param text text to be checked
     * @return true on success
     */
    private boolean validText(String text) {
        return !text.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInformation that = (UserInformation) o;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
