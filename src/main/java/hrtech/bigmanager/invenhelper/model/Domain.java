package hrtech.bigmanager.invenhelper.model;

import hrtech.bigmanager.invenhelper.exception.InvalidRepresentationOfConceptOnJSON;
import org.json.JSONObject;

/**
 * Interface to be implemented on Domain concepts
 *
 * @param <E> Entity class
 * @param <K> Entity key class
 */
public interface Domain<E, K> {

    /**
     * Method that converts a JSONObject into the domain concept
     *
     * @param jsonObject object to convert
     * @return domain concept built from the JSON representation
     */
    default E convertFromJSON(JSONObject jsonObject) throws InvalidRepresentationOfConceptOnJSON {
        throw new InvalidRepresentationOfConceptOnJSON("Converter override missing");
    }

    /**
     * This method converts the domain concept into a JSONObject
     *
     * @return JSONObject representing the domain concept
     */
    JSONObject convertToJSON();

    /**
     * Method that compares if an entity corresponds to the other, by the key
     *
     * @param otherEntity entity to compare
     * @return true if the entities are equal, false otherwise
     */
    boolean sameAs(E otherEntity);

}
