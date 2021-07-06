package hrtech.bigmanager.invenhelper.model;

import hrtech.bigmanager.invenhelper.exception.InvalidQuantity;
import hrtech.bigmanager.invenhelper.exception.InvalidText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    private UUID defaultDatabaseCode;
    private String defaultCode;
    private ProductKey defaultKey;
    private String defaultName;
    private String defaultDescription;
    private ProductInformation defaultInformation;
    private int defaultGoodQuantity;
    private int randomNumberToIncrease;
    private int randomNumberToDecrease;
    private Quantity defaultQuantity;
    private Product product;

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
        Random random = new Random();
        defaultGoodQuantity = random.ints(1, 1000).findFirst().getAsInt();
        randomNumberToIncrease = random.ints(1, 1000).findFirst().getAsInt();
        randomNumberToDecrease = random.ints(0, defaultGoodQuantity).findFirst().getAsInt();
        defaultQuantity = new Quantity(defaultGoodQuantity);

        defaultName = generateString();
        defaultDescription = generateString();
        defaultInformation = new ProductInformation(defaultName, defaultDescription);

        defaultCode = generateString();
        defaultDatabaseCode = UUID.randomUUID();
        defaultKey = new ProductKey(defaultDatabaseCode, defaultCode);

        product = new Product(defaultKey, defaultInformation, defaultQuantity);
    }

    @Test
    void increaseValidQuantity() {
        product.increaseQuantity(randomNumberToIncrease);
        assertEquals(randomNumberToIncrease + defaultGoodQuantity, product.getQuantity());
    }

    @Test
    void decreaseValidQuantity() {
        product.decreaseQuantity(randomNumberToDecrease);
        assertEquals(defaultGoodQuantity - randomNumberToDecrease, product.getQuantity());
    }

    @Test
    void decreaseInvalidQuantity() {
        assertThrows(InvalidQuantity.class, () -> product.decreaseQuantity(defaultGoodQuantity + randomNumberToDecrease));
    }

    @Test
    void changeValidName() {
        String newName = generateString();
        product.changeName(newName);
        assertEquals(newName, product.getName());
    }

    @Test
    void changeInvalidName() {
        assertThrows(InvalidText.class, () -> product.changeName("   "));
        assertThrows(InvalidText.class, () -> product.changeName(""));
    }

    @Test
    void changeValidDescription() {
        String newDescription = generateString();
        product.changeDescription(newDescription);
        assertEquals(newDescription, product.getDescription());
    }

    @Test
    void changeInvalidDescription() {
        assertThrows(InvalidText.class, () -> product.changeDescription("   "));
        assertThrows(InvalidText.class, () -> product.changeDescription(""));
    }

    @Test
    void convertToJSON() {
        assertEquals(defaultCode, product.convertToJSON().getString("identifier"));
        assertEquals(defaultName, product.convertToJSON().getString("name"));
        assertEquals(defaultDescription, product.convertToJSON().getString("description"));
        assertEquals(defaultQuantity.getQuantity(), product.convertToJSON().getInt("quantity"));
    }
}