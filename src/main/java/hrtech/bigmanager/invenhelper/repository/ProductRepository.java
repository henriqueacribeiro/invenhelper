package hrtech.bigmanager.invenhelper.repository;

import hrtech.bigmanager.invenhelper.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Product repository
 */
@Repository
public class ProductRepository extends JdbcDaoSupport implements IRepository<Product, ProductKey> {

    private final Logger logger = LoggerFactory.getLogger(ProductRepository.class);

    @Autowired
    public void initialize(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    /**
     * Method that, given a ResultSet, retrieves the info and tries to build an Entity instance wrapped on an Optional
     *
     * @param resultSet result set to extract the info
     * @return Optional with the Entity instantiated (or not, on failure)
     */
    @Override
    public Optional<Product> map(ResultSet resultSet) {
        Set<String> columnsOnResultSet = IRepository.extractResultSetColumns(resultSet, true);
        Optional<Product> product;
        try {
            if (columnsOnResultSet.isEmpty()) {
                throw new SQLException("Error extracting columns from ResultSet");
            }

            String databaseIDOnString = columnsOnResultSet.contains("product.id") ? resultSet.getString("product.id") : "";
            UUID databaseID = DomainKey.convertStringToUUID(databaseIDOnString);
            String productID = columnsOnResultSet.contains("product.business_id") ? resultSet.getString("product.business_id") : "";
            ProductKey key = new ProductKey(databaseID, productID);

            String name = columnsOnResultSet.contains("product.name") ? resultSet.getString("product.name") : "";
            String description = columnsOnResultSet.contains("product.description") ? resultSet.getString("product.description") : "";
            ProductInformation information = new ProductInformation(name, description);

            int quantityNumber = columnsOnResultSet.contains("product.quantity") ? resultSet.getInt("product.quantity") : -1;
            Quantity quantity = new Quantity(quantityNumber);

            product = Optional.of(new Product(key, information, quantity));
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error while mapping Product: " + e.getLocalizedMessage());
            product = Optional.empty();
        }
        return product;
    }

    /**
     * Method that creates a product on the database
     *
     * @param productToCreate product to be created
     * @return true on success; false otherwise
     */
    public boolean createProduct(Product productToCreate) {
        String createProduct = "INSERT INTO product (id, business_id, name, description, quantity) VALUES (?, ?, ?, ?, ?)";
        if (getJdbcTemplate() != null) {
            try {
                int result = getJdbcTemplate().update(createProduct, productToCreate.getProductID(), productToCreate.getProductBusinessKey(),
                        productToCreate.getName(), productToCreate.getDescription(), productToCreate.getQuantity());
                return result == 1;
            } catch (DataAccessException e) {
                logger.error("Error while manipulating data: " + e.getLocalizedMessage());
                return false;
            }
        } else {
            logger.error("Error while connecting to the database to create a product");
            return false;
        }
    }

    /**
     * Method that saves an entity on the repository
     *
     * @param objectToSave entity to be created/updated
     * @return entity created/updated
     */
    @Override
    public boolean save(Product objectToSave) {
        String updateProduct = "UPDATE product SET business_id = ?, name = ?, description = ?, quantity = ? WHERE id = ?";
        if (getJdbcTemplate() != null) {
            try {
                int result = getJdbcTemplate().update(updateProduct, objectToSave.getProductBusinessKey(),
                        objectToSave.getName(), objectToSave.getDescription(), objectToSave.getQuantity(), objectToSave.getProductID());
                return result == 1;
            } catch (DataAccessException e) {
                logger.error("Error while manipulating data: " + e.getLocalizedMessage());
                return false;
            }
        } else {
            logger.error("Error while connecting to the database to create a product");
            return false;
        }
    }

    /**
     * Method that, given a key object, returns the corresponding Entity on an Optional
     *
     * @param keyToSearch valid key object to search the entity
     * @return Option that contains the entity, if such ID exists on the database
     */
    @Override
    public Optional<Product> findById(ProductKey keyToSearch) {
        if (getJdbcTemplate() != null) {
            try {
                return getJdbcTemplate().queryForObject("SELECT * FROM product WHERE id = ?", (rs, rowNum) -> map(rs), keyToSearch.getDatabaseKey().toString());
            } catch (DataAccessException e) {
                logger.error("Error while manipulating data: " + e.getLocalizedMessage());
                return Optional.empty();
            }
        } else {
            logger.error("Invalid JDBC template instance");
            return Optional.empty();
        }
    }
}
