package hrtech.bigmanager.invenhelper.model;

import hrtech.bigmanager.invenhelper.exception.InvalidBusinessIdentifier;

import java.util.Objects;
import java.util.UUID;

/**
 * Key of a product. It has two keys: the database and the internal (company) key
 */
public class ProductKey implements DomainKey<ProductKey> {

    private final UUID databaseKey;
    private String internalKey;

    /**
     * This constructor creates a Product key, using a business key (that is tested) and generates a database key (UUID)
     *
     * @param internalKey internal product key
     * @throws InvalidBusinessIdentifier if the internal key is invalid
     */
    public ProductKey(String internalKey) throws InvalidBusinessIdentifier {
        this.databaseKey = DomainKey.generateID();
        if (validBusinessKey(internalKey)) {
            this.internalKey = internalKey;
        } else {
            throw new InvalidBusinessIdentifier(internalKey);
        }
    }

    /**
     * This constructor creates a Product key, using a business key (that is tested) and the database key
     *
     * @param internalKey internal product key
     * @throws InvalidBusinessIdentifier if the internal key is invalid
     */
    public ProductKey(UUID databaseKey, String internalKey) throws InvalidBusinessIdentifier {
        this.databaseKey = databaseKey;
        if (validBusinessKey(internalKey)) {
            this.internalKey = internalKey;
        } else {
            throw new InvalidBusinessIdentifier(internalKey);
        }
    }

    public UUID getDatabaseKey() {
        return databaseKey;
    }

    public String getInternalKey() {
        return internalKey;
    }

    public void setInternalKey(String internalKey) {
        this.internalKey = internalKey;
    }

    /**
     * Method that validates a text
     *
     * @param text text to be checked
     * @return true on success
     */
    private boolean validBusinessKey(String text) {
        return !text.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductKey that = (ProductKey) o;
        return getDatabaseKey().equals(that.getDatabaseKey()) && getInternalKey().equals(that.getInternalKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDatabaseKey(), getInternalKey());
    }
}
