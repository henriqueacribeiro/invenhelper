package hrtech.bigmanager.invenhelper.model.user;

import hrtech.bigmanager.invenhelper.exception.InvalidText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserInformationTest {

    private String defaultName;
    private UserInformation defaultInformation;

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
        defaultName = generateString();
        defaultInformation = new UserInformation(defaultName);
    }

    @Test
    void invalidConstruction() {
        assertThrows(InvalidText.class, () -> new UserInformation(""));
    }

    @Test
    void setValidName() {
        assertEquals(defaultName, defaultInformation.getName());
    }

    @Test
    void setInvalidName() {
        assertThrows(InvalidText.class, () -> defaultInformation.setName(""));
    }


}