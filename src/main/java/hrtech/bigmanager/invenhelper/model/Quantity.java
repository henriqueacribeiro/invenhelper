package hrtech.bigmanager.invenhelper.model;

import hrtech.bigmanager.invenhelper.exception.InvalidQuantity;

import java.util.Objects;

/**
 * Quantity. Integer wrapper
 */
public class Quantity {

    private int quantity;

    /**
     * Default constructor
     *
     * @param quantity quantity to use on construction
     * @throws InvalidQuantity if the quantity is invalid (less than zero)
     */
    public Quantity(int quantity) throws InvalidQuantity{
        if (validQuantity(quantity)) {
            this.quantity = quantity;
        } else {
            throw new InvalidQuantity(quantity);
        }
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Method that sets the quantity
     *
     * @param quantity quantity to be set
     * @throws InvalidQuantity if the quantity is invalid
     */
    private void setQuantity(int quantity) {
        if (validQuantity(quantity)) {
            this.quantity = quantity;
        } else {
            throw new InvalidQuantity(quantity);
        }
    }

    /**
     * Method that verifies if a quantity is valid
     *
     * @param quantity quantity to check
     * @return true on success; false otherwise
     */
    private boolean validQuantity(int quantity) {
        return quantity >= 0;
    }

    /**
     * Method that increases the quantity by a certain number
     *
     * @param quantityToIncrease quantity to be increased
     * @throws InvalidQuantity if the quantity is invalid
     */
    public void increaseQuantity(int quantityToIncrease) {
        setQuantity(quantity + quantityToIncrease);
    }

    /**
     * Method that decreases the quantity by a certain number
     *
     * @param quantityToDecrease quantity to be decreased
     * @throws InvalidQuantity if the quantity is invalid
     */
    public void decreaseQuantity(int quantityToDecrease) {
        setQuantity(quantity - quantityToDecrease);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity1 = (Quantity) o;
        return getQuantity() == quantity1.getQuantity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuantity());
    }
}
