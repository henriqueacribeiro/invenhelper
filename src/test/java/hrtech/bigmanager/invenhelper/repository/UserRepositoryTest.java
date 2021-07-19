package hrtech.bigmanager.invenhelper.repository;

import hrtech.bigmanager.invenhelper.config.InvenHelperApplication;
import hrtech.bigmanager.invenhelper.model.user.User;
import hrtech.bigmanager.invenhelper.model.user.UserInformation;
import hrtech.bigmanager.invenhelper.model.user.UserKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = "classpath:databaseinit.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(classes = InvenHelperApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private UUID defaultDatabaseCode;
    private String defaultCode;
    private UserKey defaultKey;
    private String defaultName;
    private UserInformation defaultInformation;
    private User user;

    private String generateString() {
        //Baeldung Random String generation
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private boolean generateRandomBoolean() {
        Random random = new Random();
        int randomInt = random.ints(1, 1000).findFirst().getAsInt();
        return randomInt % 2 == 0;
    }

    @BeforeEach
    void setUp() {
        defaultName = generateString();
        defaultInformation = new UserInformation(defaultName);

        defaultCode = generateString();
        defaultDatabaseCode = UUID.randomUUID();
        defaultKey = new UserKey(defaultDatabaseCode, defaultCode);

        user = new User(defaultKey, defaultInformation);
        user.addEntryToPermissionMap(User.UserPermission.CAN_MODIFY_PRODUCTS.getPermissionName(), generateRandomBoolean());
        user.addEntryToPermissionMap(User.UserPermission.CAN_ADD_USERS.getPermissionName(), generateRandomBoolean());
        user.addEntryToPermissionMap(User.UserPermission.CAN_MODIFY_INVENTORY.getPermissionName(), generateRandomBoolean());
    }

    @Test
    void insertAndSave() {
        assertTrue(repository.insert(user));
        Optional<User> obtainedUser = repository.findById(defaultKey);
        assertTrue(obtainedUser.isPresent());
        assertEquals(user, obtainedUser.get());

        user.updateName("TESTINGNAME");
        assertTrue(repository.save(user));
        obtainedUser = repository.findById(defaultKey);
        assertTrue(obtainedUser.isPresent());
        assertEquals(user, obtainedUser.get());
        assertTrue(user.sameAs(obtainedUser.get()));
    }

    @Test
    void findById() {
        assertTrue(repository.insert(user));
        Optional<User> obtainedUser = repository.findById(defaultKey);
        assertTrue(obtainedUser.isPresent());
        assertEquals(user, obtainedUser.get());
        assertTrue(user.sameAs(obtainedUser.get()));
    }

    @Test
    void findByUsername() {
        assertTrue(repository.insert(user));
        Optional<User> obtainedUser = repository.findByUsername(defaultKey);
        assertTrue(obtainedUser.isPresent());
        assertEquals(user, obtainedUser.get());
        assertTrue(user.sameAs(obtainedUser.get()));
    }
}