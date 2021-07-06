package hrtech.bigmanager.invenhelper.exception;

/**
 * This exception is thrown when a invalid quantity is detected
 */
public class InvalidQuantity extends IllegalArgumentException {

    /**
     * Exception constructor
     *
     * @param s exception message
     */
    public InvalidQuantity(String s) {
        super(s);
    }

    /**
     * Exception constructor. Receives quantity, throws message "Invalid quantity: " with quantity
     *
     * @param invalidQuantity quantity used on failure
     */
    public InvalidQuantity(int invalidQuantity) {
        super("Invalid quantity: " + invalidQuantity);
    }
}
