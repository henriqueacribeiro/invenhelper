package hrtech.bigmanager.invenhelper.model;

import java.util.UUID;

/**
 * Interface to be implemented on Domain Key concepts
 *
 * @param <K> Entity key class
 */
public interface DomainKey<K> {

    /**
     * Method that generates a random UUID to be used on a Entity Key
     *
     * @return random UUID generated
     */
    static UUID generateID() {
        return UUID.randomUUID();
    }

    /**
     * Method that converts a String to a UUID object. The String must be on a valid format: 32 characters separated by 5 hyphen (37 characters) or 32 characters and no hyphens
     *
     * @param idToConvert id that will be converted
     * @return UUID of the string, IllegalArgumentException on failure
     */
    static UUID convertStringToUUID(String idToConvert) {
        if (idToConvert.split("-").length != 5 && idToConvert.length() == 32) {
            return UUID.fromString(idToConvert.substring(0, 8) + "-" + idToConvert.substring(8, 12) + "-" + idToConvert.substring(12, 16) + "-" +
                    idToConvert.substring(16, 20) + "-" + idToConvert.substring(20));
        } else if (idToConvert.split("-").length == 5 && idToConvert.length() == 36) {
            return UUID.fromString(idToConvert);
        } else {
            throw new IllegalArgumentException("Invalid string to convert to UUID");
        }
    }
}
