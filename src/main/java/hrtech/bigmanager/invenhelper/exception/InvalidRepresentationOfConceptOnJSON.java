package hrtech.bigmanager.invenhelper.exception;

/**
 * This exception is thrown when a JSON object cannot be converted into a domain concept
 */
public class InvalidRepresentationOfConceptOnJSON extends IllegalArgumentException {

    /**
     * Exception constructor
     *
     * @param s exception message
     */
    public InvalidRepresentationOfConceptOnJSON(String s) {
        super(s);
    }
}
