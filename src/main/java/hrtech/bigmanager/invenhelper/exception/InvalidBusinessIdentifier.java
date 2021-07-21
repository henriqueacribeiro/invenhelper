package hrtech.bigmanager.invenhelper.exception;

/**
 * This exception is thrown when a invalid text is detected
 */
public class InvalidBusinessIdentifier extends IllegalArgumentException {

    private static final String invalidMessage = "Invalid business identifier";

    /**
     * Constructs an <code>IllegalArgumentException</code> with no
     * detail message.
     */
    public InvalidBusinessIdentifier() {
        super(invalidMessage);
    }

    /**
     * Exception constructor that accepts the error message
     *
     * @param errorMessage invalidmessage
     */
    public InvalidBusinessIdentifier(String errorMessage) {
        super((!errorMessage.isBlank() ? errorMessage : invalidMessage));
    }

}
