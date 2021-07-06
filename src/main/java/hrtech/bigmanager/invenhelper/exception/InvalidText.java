package hrtech.bigmanager.invenhelper.exception;

/**
 * This exception is thrown when a invalid text is detected
 */
public class InvalidText extends IllegalArgumentException {

    /**
     * Exception constructor
     *
     * @param s exception message
     */
    public InvalidText(String s) {
        super(s);
    }

}
