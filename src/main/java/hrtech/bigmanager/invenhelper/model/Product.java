package hrtech.bigmanager.invenhelper.model;

import hrtech.bigmanager.invenhelper.exception.InvalidBusinessIdentifier;
import hrtech.bigmanager.invenhelper.exception.InvalidQuantity;
import hrtech.bigmanager.invenhelper.exception.InvalidRepresentationOfConceptOnJSON;
import hrtech.bigmanager.invenhelper.exception.InvalidText;
import org.json.JSONObject;

import java.util.Objects;
import java.util.UUID;

/**
 * Product on the inventory
 */
public class Product implements Domain<Product, ProductKey> {

    private final ProductKey identifier;
    private final ProductInformation information;
    private final Quantity quantity;

    public Product(ProductKey identifier, ProductInformation information, Quantity quantity) {
        this.identifier = identifier;
        this.information = information;
        this.quantity = quantity;
    }

    public String getProductBusinessKey() {
        return identifier.getInternalKey();
    }

    public String getProductID() {
        return identifier.getDatabaseKey().toString();
    }

    public String getName() {
        return information.getName();
    }

    public String getDescription() {
        return information.getDescription();
    }

    public int getQuantity() {
        return quantity.getQuantity();
    }

    /**
     * Method that increases the quantity by a certain number
     *
     * @param quantityToIncrease quantity to be increased
     * @throws InvalidQuantity if the quantity is invalid
     */
    public void increaseQuantity(int quantityToIncrease) {
        quantity.increaseQuantity(quantityToIncrease);
    }

    /**
     * Method that decreases the quantity by a certain number
     *
     * @param quantityToDecrease quantity to be decreased
     * @throws InvalidQuantity if the quantity is invalid
     */
    public void decreaseQuantity(int quantityToDecrease) {
        quantity.decreaseQuantity(quantityToDecrease);
    }

    /**
     * Method that sets the name, after checking if it is valid
     *
     * @param newName name of the product
     * @throws InvalidText if the text is invalid
     */
    public void changeName(String newName) {
        information.setName(newName);
    }

    /**
     * Method that sets the description, after checking if it is valid
     *
     * @param description description of the product
     * @throws InvalidText if the text is invalid
     */
    public void changeDescription(String description) {
        information.setDescription(description);
    }

    /**
     * Method that converts a JSONObject into the domain concept
     *
     * @param jsonObject object to convert
     * @return domain concept built from the JSON representation
     */
    @Override
    public Product convertFromJSON(JSONObject jsonObject) throws InvalidRepresentationOfConceptOnJSON {
        String productInternalKey = jsonObject.optString("identifier", "");
        String productName = jsonObject.optString("name", "");
        String productDescription = jsonObject.optString("description", "");
        int quantity = jsonObject.optInt("quantity", -1);

        try {
            ProductKey key = new ProductKey(productInternalKey);
            ProductInformation info = new ProductInformation(productName, productDescription);
            Quantity quantityDomain = new Quantity(quantity);
            return new Product(key, info, quantityDomain);
        } catch (InvalidQuantity iq) {
            throw new InvalidRepresentationOfConceptOnJSON("Invalid quantity: " + quantity);
        } catch (InvalidText it) {
            throw new InvalidRepresentationOfConceptOnJSON("Invalid text found (description or name");
        }
    }

    /**
     * Method that converts a JSONObject into the domain concept. It differs from the other method since the object obtained will have the quantity at zero and a new UUID
     *
     * @param jsonObject object to convert
     * @return domain concept built from the JSON representation
     */
    public static Product convertFromJSONToCreate(JSONObject jsonObject) throws InvalidRepresentationOfConceptOnJSON {
        String productInternalKey = jsonObject.optString("identifier", "");
        String productName = jsonObject.optString("name", "");
        String productDescription = jsonObject.optString("description", "");
        int quantity = jsonObject.optInt("quantity", 0);

        try {
            ProductKey key = new ProductKey(UUID.randomUUID(), productInternalKey);
            ProductInformation info = new ProductInformation(productName, productDescription);
            Quantity quantityDomain = new Quantity(quantity);
            return new Product(key, info, quantityDomain);
        } catch (InvalidText it) {
            throw new InvalidRepresentationOfConceptOnJSON("Invalid text found (description or name");
        } catch (InvalidBusinessIdentifier ibi) {
            throw new InvalidRepresentationOfConceptOnJSON(ibi.getLocalizedMessage());
        }
    }

    /**
     * This method converts the domain concept into a JSONObject
     *
     * @return JSONObject representing the domain concept
     */
    @Override
    public JSONObject convertToJSON() {
        JSONObject answer = new JSONObject();
        answer.put("identifier", this.identifier.getInternalKey());
        answer.put("name", this.information.getName());
        answer.put("description", this.information.getDescription());
        answer.put("quantity", this.quantity.getQuantity());
        return answer;
    }

    /**
     * Method that compares if an entity corresponds to the other, by the key
     *
     * @param otherEntity entity to compare
     * @return true if the entities are equal, false otherwise
     */
    @Override
    public boolean sameAs(Product otherEntity) {
        return this.identifier.equals(otherEntity.identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return identifier.equals(product.identifier) && information.equals(product.information) && getQuantity() == product.getQuantity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, information, getQuantity());
    }
}
