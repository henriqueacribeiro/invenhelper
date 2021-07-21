package hrtech.bigmanager.invenhelper.exception;

/**
 * Exception to be thrown when username does not exist
 */
public class UserDoesNotExist extends IllegalArgumentException {

    /**
     * This constructor uses a sent message to throw the exception
     *
     * @param message message to print
     */
    public UserDoesNotExist(String message) {
        super(message);
    }

}
