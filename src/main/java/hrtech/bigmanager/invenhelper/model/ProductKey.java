package hrtech.bigmanager.invenhelper.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Key of a product. It has two keys: the database and the internal (company) key
 */
public class ProductKey implements DomainKey<ProductKey> {

    private final UUID databaseKey;
    private String internalKey;

    public ProductKey(String internalKey) {
        this.databaseKey = DomainKey.generateID();
        this.internalKey = internalKey;
    }

    public ProductKey(UUID databaseKey, String internalKey) {
        this.databaseKey = databaseKey;
        this.internalKey = internalKey;
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
