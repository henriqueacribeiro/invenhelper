package hrtech.bigmanager.invenhelper.model;

import hrtech.bigmanager.invenhelper.exception.InvalidQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuantityTest {

    private int defaultGoodQuantity;
    private int randomNumberToIncrease;
    private int randomNumberToDecrease;
    private int invalidRandomNumberToDecrease;

    @BeforeEach
    void setUp() {
        Random random = new Random();
        defaultGoodQuantity = random.ints(1, 1000).findFirst().getAsInt();
        randomNumberToIncrease = random.ints(1, 1000).findFirst().getAsInt();
        randomNumberToDecrease = random.ints(0, defaultGoodQuantity).findFirst().getAsInt();
        invalidRandomNumberToDecrease = defaultGoodQuantity + 10;
    }

    @Test
    void quantityInvalid() {
        assertThrows(InvalidQuantity.class, () -> new Quantity(-20));
    }

    @Test
    void getQuantity() {
        Quantity quantity = new Quantity(defaultGoodQuantity);
        assertEquals(defaultGoodQuantity, quantity.getQuantity());
    }

    @Test
    void increaseQuantity() {
        Quantity quantity = new Quantity(defaultGoodQuantity);
        assertEquals(defaultGoodQuantity, quantity.getQuantity());
        quantity.increaseQuantity(randomNumberToIncrease);
        assertEquals(defaultGoodQuantity + randomNumberToIncrease, quantity.getQuantity());
    }

    @Test
    void decreaseQuantityValid() {
        Quantity quantity = new Quantity(defaultGoodQuantity);
        assertEquals(defaultGoodQuantity, quantity.getQuantity());
        quantity.decreaseQuantity(randomNumberToDecrease);
        assertEquals(defaultGoodQuantity - randomNumberToDecrease, quantity.getQuantity());
    }

    @Test
    void decreaseQuantityInvalid() {
        Quantity quantity = new Quantity(defaultGoodQuantity);
        assertEquals(defaultGoodQuantity, quantity.getQuantity());
        assertThrows(InvalidQuantity.class, () -> quantity.decreaseQuantity(invalidRandomNumberToDecrease));
    }
}