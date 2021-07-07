package hrtech.bigmanager.invenhelper.model;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Response to outside or method that would be void otherwise
 * <p>
 * Can be used to improve code checks
 *
 * @param <E> Entity that may be retrieved by the domain
 */
public class Response<E extends Domain> implements Serializable {

    private final boolean success;
    private final String additionalInformation;
    private final E objectToReturn;

    public Response(boolean success) {
        this.success = success;
        this.additionalInformation = "";
        this.objectToReturn = null;
    }

    public Response(boolean success, String additionalInformation) {
        this.success = success;
        this.additionalInformation = additionalInformation;
        this.objectToReturn = null;
    }

    public Response(boolean success, String additionalInformation, E objectToReturn) {
        this.success = success;
        this.additionalInformation = additionalInformation;
        this.objectToReturn = objectToReturn;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public E getObjectToReturn() {
        return objectToReturn;
    }

    /**
     * Method that creates a JSON object that only contains info about the success
     *
     * @return JSONObject with info about the success
     */
    public JSONObject getJSONWithSuccess() {
        JSONObject objectToReturn = new JSONObject();
        objectToReturn.put("success", success);
        return objectToReturn;
    }

    /**
     * Method that creates a JSON object that contains info about the success and the additional message
     *
     * @return JSONObject with info about the success and the additional message
     */
    public JSONObject getJSONWithAndAdditionalInformation() {
        JSONObject objectToReturn = getJSONWithSuccess();
        objectToReturn.put("information", additionalInformation);
        return objectToReturn;
    }

    /**
     * Method that creates a JSON object with response info to a request with all possible info
     *
     * @return JSONObject with all the info of the Response
     */
    public JSONObject getJSONWithAllInfo() {
        JSONObject objectToReturn = getJSONWithAndAdditionalInformation();
        if (this.objectToReturn != null) {
            objectToReturn.put("object", this.objectToReturn.convertToJSON());
        }
        return objectToReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return success == response.success && additionalInformation.equals(response.additionalInformation) && Objects.equals(objectToReturn, response.objectToReturn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, additionalInformation, objectToReturn);
    }
}
