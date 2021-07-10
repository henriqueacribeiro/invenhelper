package hrtech.bigmanager.invenhelper.model.user;

import hrtech.bigmanager.invenhelper.exception.InvalidBusinessIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserKeyTest {

    private UserKey key;
    private String defaultBusinessIdentifier;
    private UUID defaultDatabaseID;

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

    @BeforeEach
    void setUp() {
        defaultBusinessIdentifier = generateString();
        defaultDatabaseID = UUID.randomUUID();
        key = new UserKey(defaultDatabaseID, defaultBusinessIdentifier);
    }

    @Test
    void setInternalKey() {
        String newInternalKey = generateString();
        key.setUsername(newInternalKey);
        assertEquals(newInternalKey, key.getUsername());
    }

    @Test
    void invalidBusinessKey() {
        String internalKey = "";
        assertThrows(InvalidBusinessIdentifier.class, () -> new UserKey(UUID.randomUUID(), internalKey));
        assertThrows(InvalidBusinessIdentifier.class, () -> new UserKey(internalKey));
    }

}