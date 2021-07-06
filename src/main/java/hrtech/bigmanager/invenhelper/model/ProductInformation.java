package hrtech.bigmanager.invenhelper.model;

import hrtech.bigmanager.invenhelper.exception.InvalidText;

/**
 * Product information. It contains name and description
 */
public class ProductInformation {

    private String name;
    private String description;

    public ProductInformation(String name, String description) {
        if (validText(name) && validText(description)) {
            this.name = name;
            this.description = description;
        } else {
            throw new InvalidText("Invalid product information");
        }
    }

    public String getName() {
        return name;
    }

    /**
     * Method that sets the name, after checking if it is valid
     *
     * @param name name of the product
     * @throws InvalidText if the text is invalid
     */
    public void setName(String name) {
        if (!validText(name)) {
            throw new InvalidText("Invalid product name: " + name);
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Method that sets the description, after checking if it is valid
     *
     * @param description description of the product
     * @throws InvalidText if the text is invalid
     */
    public void setDescription(String description) {
        if (!validText(description)) {
            throw new InvalidText("Invalid product description: " + description);
        }
        this.description = description;
    }

    /**
     * Method that validates a text
     *
     * @param text text to be checked
     * @return true on success
     */
    private boolean validText(String text) {
        return !text.isBlank();
    }
}
