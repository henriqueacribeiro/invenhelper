package hrtech.bigmanager.invenhelper.repository;

import hrtech.bigmanager.invenhelper.model.Domain;
import hrtech.bigmanager.invenhelper.model.DomainKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Interface that defines default repository behaviour
 *
 * @param <E> Entity to be managed
 * @param <K> Key of the entity to be managed
 */
public interface IRepository<E extends Domain<E, K>, K extends DomainKey<K>> {

    Logger logger = LoggerFactory.getLogger(IRepository.class);

    /**
     * Method that extracts the columns names or alias presents on a ResultSet
     *
     * @param resultSet     result set to extract columns
     * @param withTableName if true, the table name will be added on the beginning (for example, sandwich.name); false it will return only name
     * @return Set with Strings that corresponds to the column names or alias; empty on failure
     */
    static Set<String> extractResultSetColumns(ResultSet resultSet, boolean withTableName) {
        Set<String> columns = new HashSet<>();
        try {
            for (int index = 1; index <= resultSet.getMetaData().getColumnCount(); index++) {
                String toAdd = "";
                if (withTableName) {
                    toAdd = resultSet.getMetaData().getTableName(index).toLowerCase();
                    if (!toAdd.isEmpty()) {
                        toAdd += ".";
                    }
                }
                toAdd += resultSet.getMetaData().getColumnLabel(index).toLowerCase();
                columns.add(toAdd);
            }
        } catch (NullPointerException | SQLException e) {
            logger.error("Error while extracting column names of ResultSet: " + e.getLocalizedMessage());
            columns.clear();
        }
        return columns;
    }

    /**
     * Method that, given a ResultSet, retrieves the info and tries to build an Entity instance wrapped on an Optional
     *
     * @param resultSet result set to extract the info
     * @return Optional with the Entity instantiated (or not, on failure)
     */
    Optional<E> map(ResultSet resultSet);

    /**
     * Method that inserts an entity on the repository
     *
     * @param objectToSave entity to be created
     * @return true on success; false otherwise
     */
    boolean insert(E objectToSave);

    /**
     * Method that saves an entity on the repository
     *
     * @param objectToSave entity to be updated
     * @return true on success; false otherwise
     */
    boolean save(E objectToSave);

    /**
     * Method that, given a key object, returns the corresponding Entity on an Optional
     *
     * @param keyToSearch valid key object to search the entity
     * @return Option that contains the entity, if such ID exists on the database
     */
    Optional<E> findById(K keyToSearch);

}
