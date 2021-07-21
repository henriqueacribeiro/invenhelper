package hrtech.bigmanager.invenhelper.exception;

/**
 * Exception to be thrown when user is not allowed to access certain functionality
 */
public class UserNotAllowed extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public UserNotAllowed(String message) {
        super(message);
    }

    /**
     * Uses default pre-built message: The user X @user does not have permissions to @functionality
     *
     * @param user          user that tried to access
     * @param functionality functionality whose access was denied
     */
    public UserNotAllowed(String user, String functionality) {
        super("The user " + user + " does not have permissions to  " + functionality);
    }

}
