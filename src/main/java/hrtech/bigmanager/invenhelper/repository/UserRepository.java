package hrtech.bigmanager.invenhelper.repository;

import hrtech.bigmanager.invenhelper.model.DomainKey;
import hrtech.bigmanager.invenhelper.model.user.User;
import hrtech.bigmanager.invenhelper.model.user.UserInformation;
import hrtech.bigmanager.invenhelper.model.user.UserKey;
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
 * User repository
 */
@Repository
public class UserRepository extends JdbcDaoSupport implements IRepository<User, UserKey> {

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
    public Optional<User> map(ResultSet resultSet) {
        Set<String> columnsOnResultSet = IRepository.extractResultSetColumns(resultSet, true);
        Optional<User> user;
        try {
            if (columnsOnResultSet.isEmpty()) {
                throw new SQLException("Error extracting columns from ResultSet");
            }

            String databaseIDOnString = columnsOnResultSet.contains("user.id") ? resultSet.getString("user.id") : "";
            UUID databaseID = DomainKey.convertStringToUUID(databaseIDOnString);
            String username = columnsOnResultSet.contains("user.username") ? resultSet.getString("user.username") : "";
            UserKey key = new UserKey(databaseID, username);

            String name = columnsOnResultSet.contains("user.name") ? resultSet.getString("user.name") : "";
            UserInformation information = new UserInformation(name);

            User newUser = new User(key, information);

            newUser.addEntryToPermissionMap(User.CAN_MODIFY_INVENTORY, resultSet.getInt("user.can_modify_inventory") == 1);
            newUser.addEntryToPermissionMap(User.CAN_ADD_USERS, resultSet.getInt("user.can_add_users") == 1);
            newUser.addEntryToPermissionMap(User.CAN_MODIFY_PRODUCTS, resultSet.getInt("user.can_modify_products") == 1);

            user = Optional.of(newUser);
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error while mapping User: " + e.getLocalizedMessage());
            user = Optional.empty();
        }
        return user;
    }

    /**
     * Method that inserts an entity on the repository
     *
     * @param userToCreate entity to be created
     * @return true on success; false otherwise
     */
    @Override
    public boolean insert(User userToCreate) {
        String createUsers = "INSERT INTO user(id, username, name, can_modify_inventory, can_modify_products, can_add_users) VALUES (?, ?, ?, ?, ?, ?)";
        if (getJdbcTemplate() != null) {
            try {
                int result = getJdbcTemplate().update(createUsers, userToCreate.getDatabaseKey(), userToCreate.getUsername(),
                        userToCreate.getName(), userToCreate.checkUserPermission(User.CAN_MODIFY_INVENTORY) ? 1 : 0,
                        userToCreate.checkUserPermission(User.CAN_MODIFY_PRODUCTS) ? 1 : 0,
                        userToCreate.checkUserPermission(User.CAN_ADD_USERS) ? 1 : 0);
                return result == 1;
            } catch (DataAccessException e) {
                logger.error("Error while manipulating data: " + e.getLocalizedMessage());
                return false;
            }
        } else {
            logger.error("Error while connecting to the database to create a user");
            return false;
        }
    }

    /**
     * Method that saves an entity on the repository
     *
     * @param objectToSave entity to be updated
     * @return true on success; false otherwise
     */
    @Override
    public boolean save(User objectToSave) {
        String updateUser = "UPDATE user SET username = ?, name = ?, can_modify_inventory = ?, can_modify_products = ?, can_add_users = ? WHERE id = ?";
        if (getJdbcTemplate() != null) {
            try {
                int result = getJdbcTemplate().update(updateUser, objectToSave.getUsername(),
                        objectToSave.getName(), objectToSave.checkUserPermission(User.CAN_MODIFY_INVENTORY) ? 1 : 0,
                        objectToSave.checkUserPermission(User.CAN_MODIFY_PRODUCTS) ? 1 : 0,
                        objectToSave.checkUserPermission(User.CAN_ADD_USERS) ? 1 : 0,
                        objectToSave.getDatabaseKey().toString());
                return result == 1;
            } catch (DataAccessException e) {
                logger.error("Error while manipulating data: " + e.getLocalizedMessage());
                return false;
            }
        } else {
            logger.error("Error while connecting to the database to update a product");
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
    public Optional<User> findById(UserKey keyToSearch) {
        if (getJdbcTemplate() != null) {
            try {
                return getJdbcTemplate().queryForObject("SELECT * FROM user WHERE id = ?", (rs, rowNum) -> map(rs), keyToSearch.getDatabaseKey().toString());
            } catch (DataAccessException e) {
                logger.error("Error while manipulating data: " + e.getLocalizedMessage());
                return Optional.empty();
            }
        } else {
            logger.error("Invalid JDBC template instance");
            return Optional.empty();
        }
    }

    /**
     * Method that, given a key object, returns the corresponding object using the username
     *
     * @param keyToSearch valid key object to search the entity
     * @return Option that contains the entity, if such username exists on the database
     */
    public Optional<User> findByUsername(UserKey keyToSearch) {
        if (getJdbcTemplate() != null) {
            try {
                return getJdbcTemplate().queryForObject("SELECT * FROM user WHERE username = ?", (rs, rowNum) -> map(rs), keyToSearch.getUsername());
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
